package com.redpred.livingsystem.service.radiation;

import com.redpred.livingsystem.data.RadiationDefinitionReloadListener;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.CauseSnapshot;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.RadiationExposureState;
import com.redpred.livingsystem.domain.effect.RadiationType;
import com.redpred.livingsystem.rule.definition.RadiationDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * {@link RadiationEngine} 默认实现（阶段五）。
 *
 * <p>按辐射类型合并累积剂量（每类型一个暴露实例），剂量随时间按定义衰减/排出；超过症状阈值后严重度
 * 在阈值与致死剂量之间线性上升，达致死剂量则致死。定义按类型名（小写）查询，约定
 * {@code data/livingsystem/radiation_definition/<type>.json}（见 {@link RadiationDefinition}）。</p>
 */
public final class DefaultRadiationEngine implements RadiationEngine {

    @Override
    public void irradiate(ServerPlayer player, RadiationType type, float dose) {
        if (dose <= 0.0F) {
            return;
        }
        PlayerHealthData data = com.redpred.livingsystem.service.LivingServices.REPOSITORY.get(player);
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof RadiationExposureState rad && rad.active() && rad.getRadiationType() == type) {
                rad.setAccumulatedDose(rad.getAccumulatedDose() + dose);
                return;
            }
        }
        RadiationExposureState state = new RadiationExposureState(
                UUID.randomUUID(), UUID.randomUUID(), CauseSnapshot.UNKNOWN, type, player.level().getGameTime());
        state.setAccumulatedDose(dose);
        data.activeEffects().put(state.id(), state);
    }

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        long now = player.level().getGameTime();
        List<UUID> cleared = new ArrayList<>();
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (!(effect instanceof RadiationExposureState rad)) {
                continue;
            }
            RadiationDefinition def = definitionFor(rad.getRadiationType());
            float symptomThreshold = def != null ? def.symptomThreshold() : 0.3F;
            float lethalDose = def != null ? def.lethalDose() : 0.0F;
            float decayRate = def != null ? def.decayRate() : 0.01F;

            rad.setAccumulatedDose(Math.max(0.0F, rad.getAccumulatedDose() * (1.0F - decayRate)));
            float dose = rad.getAccumulatedDose();
            float ceiling = lethalDose > symptomThreshold ? lethalDose : 1.0F;
            rad.setSeverity(dose <= symptomThreshold ? 0.0F
                    : Mth.clamp((dose - symptomThreshold) / (ceiling - symptomThreshold), 0.0F, 1.0F));
            rad.setLastUpdatedGameTime(now);
            if (dose < 0.01F) {
                rad.setActive(false);
                cleared.add(rad.id());
            }
        }
        cleared.forEach(id -> data.activeEffects().remove(id));
    }

    @Override
    public float aggregateBurden(PlayerHealthData data) {
        float sum = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof RadiationExposureState rad && rad.active()) {
                sum += rad.severity();
            }
        }
        return Mth.clamp(sum, 0.0F, 1.0F);
    }

    /** 按辐射类型名（小写）查询定义。 */
    private static RadiationDefinition definitionFor(RadiationType type) {
        return RadiationDefinitionReloadListener.get(
                ResourceLocation.fromNamespaceAndPath("livingsystem", type.name().toLowerCase(Locale.ROOT)));
    }
}
