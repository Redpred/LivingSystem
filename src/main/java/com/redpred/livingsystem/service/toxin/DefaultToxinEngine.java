package com.redpred.livingsystem.service.toxin;

import com.redpred.livingsystem.data.ToxinDefinitionReloadListener;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.CauseSnapshot;
import com.redpred.livingsystem.domain.effect.ExposureRoute;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.ToxicExposureState;
import com.redpred.livingsystem.rule.definition.ToxinDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@link ToxinEngine} 默认实现（阶段五）。
 *
 * <p>毒素不在接触时立即改最终状态，而是创建/累加 {@link ToxicExposureState}，随药代逐周期吸收
 * （未吸收→已吸收）与代谢清除；严重度由已吸收量乘毒性系数得出。全身毒素负荷由全部活动实例汇总，
 * 供症状（恶心/虚弱）与死亡判定读取，不在生理状态中重复保存（见 §5.8）。</p>
 */
public final class DefaultToxinEngine implements ToxinEngine {

    @Override
    public void expose(ServerPlayer player, ResourceLocation toxinId, ExposureRoute route, float dose) {
        if (dose <= 0.0F) {
            return;
        }
        PlayerHealthData data = com.redpred.livingsystem.service.LivingServices.REPOSITORY.get(player);
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof ToxicExposureState toxic && toxic.active() && toxinId.equals(toxic.getToxinId())) {
                toxic.setUnabsorbedAmount(toxic.getUnabsorbedAmount() + dose);
                return;
            }
        }
        ToxicExposureState state = new ToxicExposureState(
                UUID.randomUUID(), UUID.randomUUID(), CauseSnapshot.UNKNOWN, toxinId, route, player.level().getGameTime());
        state.setUnabsorbedAmount(dose);
        data.activeEffects().put(state.id(), state);
    }

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        List<UUID> cleared = new ArrayList<>();
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (!(effect instanceof ToxicExposureState toxic)) {
                continue;
            }
            ToxinDefinition def = ToxinDefinitionReloadListener.get(toxic.getToxinId());
            float absorptionRate = def != null ? def.absorptionRate() : 0.2F;
            float metabolismRate = def != null ? def.metabolismRate() : 0.05F;
            float potency = def != null ? def.potency() : 1.0F;

            float absorb = toxic.getUnabsorbedAmount() * absorptionRate;
            toxic.setUnabsorbedAmount(toxic.getUnabsorbedAmount() - absorb);
            toxic.setAbsorbedAmount(toxic.getAbsorbedAmount() + absorb);

            float metabolize = toxic.getAbsorbedAmount() * metabolismRate;
            toxic.setAbsorbedAmount(toxic.getAbsorbedAmount() - metabolize);
            toxic.setMetabolizedAmount(toxic.getMetabolizedAmount() + metabolize);

            toxic.setSeverity(Mth.clamp(toxic.getAbsorbedAmount() * potency, 0.0F, 1.0F));
            toxic.setLastUpdatedGameTime(player.level().getGameTime());

            if (toxic.getUnabsorbedAmount() < 0.01F && toxic.getAbsorbedAmount() < 0.01F) {
                toxic.setActive(false);
                cleared.add(toxic.id());
            }
        }
        cleared.forEach(id -> data.activeEffects().remove(id));
    }

    @Override
    public float aggregateBurden(PlayerHealthData data) {
        float sum = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof ToxicExposureState toxic && toxic.active()) {
                sum += toxic.severity();
            }
        }
        return Mth.clamp(sum, 0.0F, 1.0F);
    }
}
