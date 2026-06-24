package com.redpred.livingsystem.network.snapshot;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.body.StructureState;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.effect.TraumaKind;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.domain.treatment.AppliedTreatmentState;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload.InjuryEntry;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload.VitalsSummary;
import com.redpred.livingsystem.service.physiology.DefaultPhysiologyEngine;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * 由权威 {@link PlayerHealthData} 构建健康主界面只读快照（服务端，见开发文档 §15.2、§27）。
 *
 * <p>部位综合严重度取该部位伤势严重度与结构损伤的较大值；伤势条目展示部位、类型、严重度、是否出血、
 * 骨折等级与是否已治疗，属自查可观察级别信息。</p>
 */
public final class HealthScreenSnapshotFactory {

    private HealthScreenSnapshotFactory() {
    }

    public static HealthScreenSnapshotPayload build(PlayerHealthData data) {
        List<Float> regionSeverities = new ArrayList<>(BodyRegion.VALUES.length);
        for (BodyRegion region : BodyRegion.VALUES) {
            regionSeverities.add(regionSeverity(data, region));
        }
        return new HealthScreenSnapshotPayload(regionSeverities, vitals(data), injuries(data));
    }

    private static float regionSeverity(PlayerHealthData data, BodyRegion region) {
        float sev = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof TraumaInjuryState trauma && trauma.getBodyRegion() == region) {
                sev = Math.max(sev, trauma.severity());
            }
        }
        BodyRegionState regionState = data.bodyRegions().get(region);
        if (regionState != null) {
            for (StructureState state : regionState.getStructures().values()) {
                sev = Math.max(sev, 1.0F - state.getIntegrity());
            }
        }
        return Mth.clamp(sev, 0.0F, 1.0F);
    }

    private static VitalsSummary vitals(PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float blood = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;
        float stamina = p.getMaxStamina() > 0 ? p.getCurrentStamina() / p.getMaxStamina() : 1.0F;
        float hydration = Mth.clamp(p.getHydration() / 100.0F, 0.0F, 1.0F);
        float pain = DefaultPhysiologyEngine.aggregateTotalPain(data, p);
        return new VitalsSummary(blood, stamina, hydration, p.getRespiratoryReserve(), pain, p.getConsciousness());
    }

    private static List<InjuryEntry> injuries(PlayerHealthData data) {
        List<InjuryEntry> list = new ArrayList<>();
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (!(effect instanceof TraumaInjuryState trauma)) {
                continue;
            }
            boolean treated = trauma.getAppliedTreatments().stream().anyMatch(AppliedTreatmentState::isActive);
            list.add(new InjuryEntry(
                    trauma.getBodyRegion().ordinal(),
                    label(trauma.getTraumaKind()),
                    trauma.severity(),
                    trauma.getBleeding().isCurrentlyBleeding(),
                    trauma.getFracture().getGrade(),
                    treated));
        }
        return list;
    }

    /** 创伤类型的中文展示标签。 */
    private static String label(TraumaKind kind) {
        return switch (kind) {
            case ABRASION -> "擦伤";
            case CUT_WOUND -> "切割伤";
            case PUNCTURE_WOUND -> "刺伤";
            case PENETRATING_WOUND -> "穿透伤";
            case BALLISTIC_WOUND -> "弹道伤";
            case CONTUSION -> "挫伤";
            case CRUSH_INJURY -> "挤压伤";
            case FRACTURE -> "骨折";
            case CONCUSSION -> "脑震荡";
            case BLAST_TRAUMA -> "爆震伤";
        };
    }
}
