package com.redpred.livingsystem.service.resource;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.data.DamageProfileReloadListener;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.rule.definition.DamageProfile;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.hit.HitLocationResult;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link VanillaResourceBridge} 默认实现。阶段二 2.1/2.2：命中→创伤→结构损伤+急性失血写入聚合根；
 * 原版生命/饥饿钉哨兵。完整呼吸/代谢替换在 2.4/2.7。
 */
public final class DefaultVanillaResourceBridge implements VanillaResourceBridge {

    private final Set<UUID> handlingDeath = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isHandlingDeath(ServerPlayer player) {
        return handlingDeath.contains(player.getUUID());
    }

    @Override
    public void beginDeathHandling(ServerPlayer player) {
        handlingDeath.add(player.getUUID());
    }

    @Override
    public void endDeathHandling(ServerPlayer player) {
        handlingDeath.remove(player.getUUID());
    }

    @Override
    public void handleIncoming(ServerPlayer player, DamageContext context) {
        PlayerHealthData data = LivingServices.REPOSITORY.get(player);
        HitLocationResult location = LivingServices.HIT_LOCATION.resolve(context);
        List<HealthEffectInstance> effects = LivingServices.HEALTH_EFFECT.create(context, location);

        DamageProfile profile = DamageProfileReloadListener.get(context.source().getMsgId());
        float bloodPerSeverity = profile != null ? profile.bloodLossPerSeverity() : 200.0F;

        for (HealthEffectInstance effect : effects) {
            data.activeEffects().put(effect.id(), effect);
            if (effect instanceof TraumaInjuryState trauma) {
                BodyRegionState regionState = data.bodyRegions().get(trauma.getBodyRegion());
                if (regionState != null) {
                    regionState.getActiveEffectIds().add(trauma.id());
                }
                trauma.getStructureDamage().forEach((structure, dmg) ->
                        LivingServices.STRUCTURE.applyStructureDamage(data, trauma.getBodyRegion(), structure, dmg));
                PhysiologyState physiology = data.physiology();
                float loss = trauma.severity() * bloodPerSeverity;
                physiology.setCurrentBloodVolume(Math.max(0.0F, physiology.getCurrentBloodVolume() - loss));

                if (ModConfigs.DEBUG_CHAT.get()) {
                    player.displayClientMessage(Component.literal(String.format(
                            "§7[LS] 命中 %s/%s 严重度%.2f 失血%.0fmL → 血容量%.0f",
                            trauma.getBodyRegion(), trauma.getTraumaKind(), trauma.severity(), loss,
                            physiology.getCurrentBloodVolume())), false);
                    if (trauma.getFracture().getGrade() > 0) {
                        player.displayClientMessage(Component.literal(String.format(
                                "§e[LS] 骨折 等级%d%s",
                                trauma.getFracture().getGrade(),
                                trauma.getFracture().isDisplaced() ? " (移位)" : "")), false);
                    }
                }
            }
        }
    }

    @Override
    public void syncVanillaResources(ServerPlayer player, PlayerHealthData data) {
        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
        if (player.getFoodData().getFoodLevel() < 20) {
            player.getFoodData().setFoodLevel(20);
        }
        // 溺水屏蔽：呼吸由 respiratoryReserve 自管理，钉满原版空气值避免触发原版气泡消耗与溺水扣血（§7.5）。
        if (player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getMaxAirSupply());
        }
    }
}
