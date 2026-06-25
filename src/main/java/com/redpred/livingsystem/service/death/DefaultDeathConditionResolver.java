package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.body.StructureState;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link DeathConditionResolver} 默认实现（唯一死亡判定入口，见开发文档 §7 死亡、§17 不变量 3）。
 *
 * <p>阶段二 2.1 判定：失血低于不可维持比例，或心脏/脑结构完整度归零。2.6 增加缺氧终末（氧债满）。
 * 其余条件（循环崩溃、毒素/感染致死等）随对应子里程碑接入。</p>
 */
public final class DefaultDeathConditionResolver implements DeathConditionResolver {

    @Override
    public boolean shouldDie(ServerPlayer player, PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float fraction = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;
        if (fraction < ModConfigs.UNSURVIVABLE_BLOOD_FRACTION.get().floatValue()) {
            return true;
        }
        // 缺氧终末：氧债累积至上限（取代原版溺水致死，见 §7.5/§8.6）。
        if (p.getOxygenDebt() >= 1.0F) {
            return true;
        }
        // 毒素致死：任一毒素的严重度达到其致死阈值。
        if (lethalToxin(data)) {
            return true;
        }
        // 感染致死：任一感染的病原体载量达到其致死载量。
        if (lethalInfection(data)) {
            return true;
        }
        // 辐射致死：任一辐射暴露的累计剂量达到其致死剂量。
        if (lethalRadiation(data)) {
            return true;
        }
        return destroyed(data, BodyRegion.CHEST, AnatomicalStructure.HEART)
                || destroyed(data, BodyRegion.HEAD_NECK, AnatomicalStructure.BRAIN);
    }

    /** 任一活动毒素的严重度达到其致死阈值即视为毒素致死。 */
    private static boolean lethalToxin(PlayerHealthData data) {
        for (com.redpred.livingsystem.domain.effect.HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof com.redpred.livingsystem.domain.effect.ToxicExposureState toxic && toxic.active()) {
                com.redpred.livingsystem.rule.definition.ToxinDefinition def =
                        com.redpred.livingsystem.data.ToxinDefinitionReloadListener.get(toxic.getToxinId());
                if (def != null && def.lethalThreshold() > 0.0F && toxic.severity() >= def.lethalThreshold()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 任一活动感染的病原体载量达到其致死载量即视为感染致死。 */
    private static boolean lethalInfection(PlayerHealthData data) {
        for (com.redpred.livingsystem.domain.effect.HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof com.redpred.livingsystem.domain.effect.PathogenState ps && ps.active()) {
                com.redpred.livingsystem.rule.definition.PathogenDefinition def =
                        com.redpred.livingsystem.data.PathogenDefinitionReloadListener.get(ps.getPathogenId());
                if (def != null && def.lethalLoad() > 0.0F && ps.getPathogenLoad() >= def.lethalLoad()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 任一活动辐射暴露的累计剂量达到其致死剂量即视为辐射致死。 */
    private static boolean lethalRadiation(PlayerHealthData data) {
        for (com.redpred.livingsystem.domain.effect.HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof com.redpred.livingsystem.domain.effect.RadiationExposureState rad && rad.active()) {
                com.redpred.livingsystem.rule.definition.RadiationDefinition def =
                        com.redpred.livingsystem.data.RadiationDefinitionReloadListener.get(
                                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                        "livingsystem", rad.getRadiationType().name().toLowerCase(java.util.Locale.ROOT)));
                if (def != null && def.lethalDose() > 0.0F && rad.getAccumulatedDose() >= def.lethalDose()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean destroyed(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure) {
        BodyRegionState regionState = data.bodyRegions().get(region);
        if (regionState == null) {
            return false;
        }
        StructureState state = regionState.structure(structure);
        return state != null && state.getIntegrity() <= 0.0F;
    }
}
