package com.redpred.livingsystem.compatibility.vanilla;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.data.TreatmentDefinitionReloadListener;
import com.redpred.livingsystem.domain.treatment.TreatmentSession;
import com.redpred.livingsystem.rule.definition.TreatmentDefinition;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.context.TreatmentContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

/**
 * 医疗物品交互入口（见开发文档 §13、§16.1）。
 *
 * <p>服务端权威：手持医疗物品右键时，若该物品 ID 存在同名治疗定义，则对自身发起治疗（自疗）。
 * 目标创伤由治疗服务按定义自动选取并校验。发起成功后消耗一个物品（创造模式不消耗）。
 * 队友治疗（指向其他玩家）与界面发起在后续子里程碑接入。</p>
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID)
public final class MedicalItemInteractionHandler {

    private MedicalItemInteractionHandler() {
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!ModConfigs.MASTER_ENABLED.get()) {
            return;
        }
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        // 临时诊断：右键通用医疗物资对自己做一次体征自检（阶段六将由专用检查器械替代）。
        if (ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "medical_supply").equals(itemId)) {
            LivingServices.EXAMINATION.examine(player, player, itemId).ifPresent(obs -> {
                LivingServices.OBSERVATIONS.store(player, obs);
                obs.values().values().forEach(v ->
                        player.displayClientMessage(Component.literal("§b[自检] " + v.displayZhCn()), false));
            });
            event.setCanceled(true);
            return;
        }

        TreatmentDefinition def = TreatmentDefinitionReloadListener.get(itemId);
        if (def == null || !def.enabled()) {
            return;
        }

        TreatmentContext context = new TreatmentContext(
                player, player, itemId, itemId, null, null, player.level().getGameTime());
        Optional<TreatmentSession> session = LivingServices.TREATMENT.startTreatment(context);
        if (session.isPresent()) {
            event.setCanceled(true);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            if (ModConfigs.DEBUG_CHAT.get()) {
                player.displayClientMessage(Component.literal("§a[LS] 开始治疗：" + def.descriptionZhCn()), true);
            }
        } else if (ModConfigs.DEBUG_CHAT.get()) {
            player.displayClientMessage(Component.literal("§7[LS] 没有可用该物品处理的伤势"), true);
        }
    }

    /** 摄入物（食物/药片）食用完成后追加 LivingSystem 效果：补充资源或进入药代。 */
    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!ModConfigs.MASTER_ENABLED.get()) {
            return;
        }
        if (event.getEntity().level().isClientSide() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingServices.CONSUMABLE.applyConsumable(player, event.getItem());
    }
}
