package com.redpred.livingsystem.compatibility.vanilla;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.client.state.ClientHealthState;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.physiology.ActivitySnapshot;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.domain.symptom.GameplayEffectSnapshot;
import com.redpred.livingsystem.domain.symptom.SymptomSnapshot;
import com.redpred.livingsystem.network.payload.HudSummaryPayload;
import com.redpred.livingsystem.network.payload.SyncGameplayPayload;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.resource.VanillaResourceBridge;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 原版伤害拦截与桥接接入（见开发文档 §7.3/§7.6）。所有取消/转换/死亡集中于此一处（§17 桥接集中不变量）。
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID)
public final class VanillaDamageInterceptor {

    private static final ResourceLocation MOVE_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "gameplay_move");
    private static final ResourceLocation ATTACK_SPEED_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "gameplay_attack_speed");
    private static final ResourceLocation JUMP_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "gameplay_jump");

    private VanillaDamageInterceptor() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!ModConfigs.MASTER_ENABLED.get()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player) || player.isCreative() || player.isSpectator()) {
            return;
        }
        DamageSource source = event.getSource();
        VanillaResourceBridge bridge = LivingServices.VANILLA_BRIDGE;
        if (bridge.isHandlingDeath(player) || source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return;
        }
        float amount = event.getAmount();
        if (amount > 0) {
            DamageContext context = LivingServices.DAMAGE_CONTEXT.create(player, source, amount, player.level().getGameTime());
            bridge.handleIncoming(player, context);
        }
        event.setAmount(0.0F);
        for (DamageContainer.Reduction reduction : DamageContainer.Reduction.values()) {
            event.addReductionModifier(reduction, (container, value) -> 0.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!ModConfigs.MASTER_ENABLED.get()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player) || player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.tickCount % ModConfigs.TICK_INTERVAL.get() != 0) {
            return;
        }
        VanillaResourceBridge bridge = LivingServices.VANILLA_BRIDGE;
        if (bridge.isHandlingDeath(player)) {
            return;
        }
        PlayerHealthData data = LivingServices.REPOSITORY.get(player);
        LivingServices.PHYSIOLOGY.runCycle(player, data, sampleActivity(player));

        if (LivingServices.DEATH.shouldDie(player, data)) {
            if (ModConfigs.DEBUG_CHAT.get()) {
                player.displayClientMessage(Component.literal("§c[LS] 死亡：失血或要害失能"), false);
            }
            LivingServices.DEATH_REPORT.buildReport(player, data).ifPresent(report -> data.deathReports().add(report));
            bridge.beginDeathHandling(player);
            player.hurt(player.damageSources().genericKill(), Float.MAX_VALUE);
            bridge.endDeathHandling(player);
        } else {
            bridge.syncVanillaResources(player, data);
            SymptomSnapshot symptoms = LivingServices.SYMPTOM.computeSymptoms(player, data);
            GameplayEffectSnapshot gameplay = LivingServices.GAMEPLAY.aggregate(symptoms);
            data.setGameplay(gameplay);
            applyGameplay(player, gameplay);
            sendHudSummary(player, data);
            sendGameplay(player, gameplay);
        }
    }

    /** 挖掘速度减免（双端一致）：服务端读权威聚合根，客户端读同步快照，避免挖掘预测回弹。 */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!ModConfigs.MASTER_ENABLED.get()) {
            return;
        }
        Player player = event.getEntity();
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        float mining;
        if (player.level().isClientSide()) {
            mining = ClientHealthState.gameplayMining();
        } else if (player instanceof ServerPlayer serverPlayer) {
            mining = LivingServices.REPOSITORY.get(serverPlayer).gameplay().miningSpeedMultiplier();
        } else {
            return;
        }
        if (mining < 0.999F) {
            event.setNewSpeed(event.getNewSpeed() * mining);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            GameRules.BooleanValue rule = level.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION);
            if (rule.get()) {
                rule.set(false, level.getServer());
            }
        }
    }

    /** 把游戏性输出应用为属性修饰（移动/攻速/跳跃）与疾跑限制；挖掘经 BreakSpeed、镜头/心跳经客户端表现。 */
    private static void applyGameplay(ServerPlayer player, GameplayEffectSnapshot effect) {
        applyMultiplier(player, Attributes.MOVEMENT_SPEED, MOVE_MODIFIER, effect.movementSpeedMultiplier());
        applyMultiplier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER, effect.attackSpeedMultiplier());
        applyMultiplier(player, Attributes.JUMP_STRENGTH, JUMP_MODIFIER, effect.jumpStrengthMultiplier());
        if (!effect.sprintAllowed() && player.isSprinting()) {
            player.setSprinting(false);
        }
    }

    private static void applyMultiplier(ServerPlayer player, Holder<Attribute> attribute, ResourceLocation id, float multiplier) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(id);
        if (multiplier < 0.999F) {
            instance.addTransientModifier(new AttributeModifier(id, multiplier - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    /** 采集玩家当前活动作为下一轮生理计算的输入（见开发文档 §6.4）。服务端无按键输入，跳跃由垂直速度近似。 */
    private static ActivitySnapshot sampleActivity(ServerPlayer player) {
        boolean sprinting = player.isSprinting();
        boolean swimming = player.isInWater() || player.isUnderWater();
        boolean onGround = player.onGround();
        net.minecraft.world.phys.Vec3 mv = player.getDeltaMovement();
        float horizontal = (float) Math.sqrt(mv.x * mv.x + mv.z * mv.z);
        boolean jumping = !onGround && mv.y > 0.0;
        boolean walking = onGround && horizontal > 0.01F && !sprinting;
        boolean resting = onGround && horizontal <= 0.01F && !sprinting;
        boolean usingMain = player.isUsingItem() && player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND;
        boolean usingOff = player.isUsingItem() && player.getUsedItemHand() == net.minecraft.world.InteractionHand.OFF_HAND;
        return new ActivitySnapshot(resting, walking, sprinting, jumping, swimming, usingMain, usingOff, horizontal);
    }

    private static void sendHudSummary(ServerPlayer player, PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float blood = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() * 20.0F : 20.0F;
        float stamina = p.getMaxStamina() > 0 ? p.getCurrentStamina() / p.getMaxStamina() * 20.0F : 20.0F;
        float hydration = p.getHydration() / 100.0F * 20.0F;
        float respiratory = p.getRespiratoryReserve() * 20.0F;
        PacketDistributor.sendToPlayer(player, new HudSummaryPayload(blood, stamina, hydration, respiratory));
    }

    private static void sendGameplay(ServerPlayer player, GameplayEffectSnapshot effect) {
        PacketDistributor.sendToPlayer(player, new SyncGameplayPayload(
                effect.miningSpeedMultiplier(), effect.cameraSway(), effect.heartbeatAudioIntensity()));
    }
}
