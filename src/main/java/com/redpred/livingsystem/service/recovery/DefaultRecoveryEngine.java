package com.redpred.livingsystem.service.recovery;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.effect.BleedingState;
import com.redpred.livingsystem.domain.effect.ContaminationState;
import com.redpred.livingsystem.domain.effect.HealingState;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.domain.treatment.AppliedTreatmentState;
import com.redpred.livingsystem.domain.treatment.TreatmentSlot;
import com.redpred.livingsystem.service.LivingServices;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@link RecoveryEngine} 默认实现（阶段三 3.4）。
 *
 * <p>按全身恢复能力（由灌注、能量、水分、营养、氧合综合派生）推进自然恢复：缓慢造血回补血容量；
 * 对已稳定（出血受控、污染达标）的创伤推进愈合进度，逐步降低严重度并修复其损伤的结构；缝合等闭合治疗
 * 加速愈合，剧烈活动减缓愈合；愈合完成的伤势被移除。脱水/缺营养会显著拉低恢复能力。</p>
 */
public final class DefaultRecoveryEngine implements RecoveryEngine {

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float minutes = ModConfigs.TICK_INTERVAL.get() / 1200.0F;
        float capacity = recoveryCapacity(p);
        regenerateBlood(p, capacity, minutes);
        healInjuries(player, data, capacity, minutes);
    }

    /** 综合恢复能力：灌注、能量、水分、营养、氧合的加权派生（0~1）。 */
    private static float recoveryCapacity(PhysiologyState p) {
        float perfusion = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;
        float energy = Mth.clamp(p.getMetabolicEnergy() / 100.0F, 0.0F, 1.0F);
        float hydration = Mth.clamp(p.getHydration() / 100.0F, 0.0F, 1.0F);
        float nutrition = Mth.clamp(p.getNutrition() / 100.0F, 0.0F, 1.0F);
        float oxygen = Mth.clamp(1.0F - p.getOxygenDebt(), 0.0F, 1.0F);
        return Mth.clamp(perfusion * 0.30F + energy * 0.20F + hydration * 0.15F
                + nutrition * 0.20F + oxygen * 0.15F, 0.0F, 1.0F);
    }

    /** 造血：未满且能量/水分充足时按恢复能力缓慢回补血容量。 */
    private static void regenerateBlood(PhysiologyState p, float capacity, float minutes) {
        if (p.getCurrentBloodVolume() < p.getMaxBloodVolume() && capacity > 0.1F
                && p.getNutrition() > 0.0F && p.getHydration() > 0.0F) {
            float regen = 60.0F * capacity * minutes;
            p.setCurrentBloodVolume(Math.min(p.getMaxBloodVolume(), p.getCurrentBloodVolume() + regen));
        }
    }

    /** 推进各创伤的愈合：稳定后按能力/治疗/活动推进进度，降低严重度并修复结构，完成后移除。 */
    private static void healInjuries(ServerPlayer player, PlayerHealthData data, float capacity, float minutes) {
        boolean strenuous = player.isSprinting() || !player.onGround();
        List<UUID> resolved = new ArrayList<>();
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (!(effect instanceof TraumaInjuryState trauma)) {
                continue;
            }
            BleedingState b = trauma.getBleeding();
            ContaminationState c = trauma.getContamination();
            HealingState h = trauma.getHealing();

            boolean bleedingControlled = !b.isCurrentlyBleeding() || b.getClotProgress() >= 1.0F;
            boolean clean = c.getContaminationLevel() < 0.3F;
            h.setStabilized(bleedingControlled && clean);
            if (!h.isStabilized() || capacity <= 0.05F) {
                continue;
            }

            float treatMul = 1.0F;
            for (AppliedTreatmentState applied : trauma.getAppliedTreatments()) {
                if (applied.isActive() && applied.getSlot() == TreatmentSlot.WOUND_CLOSURE) {
                    treatMul = 1.5F;
                }
            }
            float activityMul = strenuous ? 0.3F : 1.0F;
            float step = 0.5F * capacity * treatMul * activityMul * minutes;
            if (step <= 0.0F) {
                continue;
            }

            h.setRecoveryProgress(Math.min(1.0F, h.getRecoveryProgress() + step));
            trauma.setSeverity(trauma.severity() * (1.0F - step));
            // 同步修复该伤势损伤过的结构。
            BodyRegionState regionState = data.bodyRegions().get(trauma.getBodyRegion());
            if (regionState != null) {
                trauma.getStructureDamage().forEach((structure, dmg) ->
                        LivingServices.STRUCTURE.applyStructureRepair(
                                data, trauma.getBodyRegion(), structure, dmg * step));
            }
            // 骨折随愈合稳定：不稳定度下降，进度足够时降级。
            if (trauma.getFracture().getGrade() > 0) {
                trauma.getFracture().setInstability(trauma.getFracture().getInstability() * (1.0F - step));
                if (h.getRecoveryProgress() > 0.8F) {
                    trauma.getFracture().setGrade(trauma.getFracture().getGrade() - 1);
                    h.setRecoveryProgress(0.5F);
                }
            }

            if (h.getRecoveryProgress() >= 1.0F || trauma.severity() < 0.02F) {
                if (trauma.getFracture().getGrade() <= 0) {
                    trauma.setActive(false);
                    resolved.add(trauma.id());
                    if (regionState != null) {
                        regionState.getActiveEffectIds().remove(trauma.id());
                    }
                }
            }
        }
        resolved.forEach(id -> data.activeEffects().remove(id));
    }
}
