package com.redpred.livingsystem.service.symptom;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.domain.symptom.SymptomSnapshot;
import com.redpred.livingsystem.domain.symptom.SymptomState;
import com.redpred.livingsystem.domain.symptom.SymptomTier;
import com.redpred.livingsystem.service.physiology.DefaultPhysiologyEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * {@link SymptomEngine} 默认实现（阶段二 2.5 起步）。
 *
 * <p>从权威健康状态单向只读地推导症状：低灌注→失血性虚弱、腿部创伤→行动障碍、臂部创伤→操作障碍。
 * 不反向修改任何健康状态。症状强度后续将映射为游戏性输出（见 {@code GameplayEffectAggregator}）。</p>
 */
public final class DefaultSymptomEngine implements SymptomEngine {

    @Override
    public SymptomSnapshot computeSymptoms(ServerPlayer player, PlayerHealthData data) {
        List<SymptomState> symptoms = new ArrayList<>();
        PhysiologyState p = data.physiology();

        float perfusion = p.getMaxBloodVolume() > 0
                ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;
        if (perfusion < 0.6F) {
            float intensity = Mth.clamp((0.6F - perfusion) / 0.6F, 0.0F, 1.0F);
            symptoms.add(symptom("hypovolemic_weakness", intensity, Optional.empty()));
        }

        float leg = maxTraumaSeverity(data, BodyRegion.LEFT_LEG, BodyRegion.RIGHT_LEG);
        if (leg > 0.3F) {
            symptoms.add(symptom("leg_impairment", leg, Optional.empty()));
        }
        // 腿部骨折 → 跛行（独立于普通腿部障碍，强度由骨折等级与不稳定推导）。
        float legFracture = maxFractureSeverity(data, BodyRegion.LEFT_LEG, BodyRegion.RIGHT_LEG);
        if (legFracture > 0.0F) {
            symptoms.add(symptom("limp", legFracture, Optional.empty()));
        }

        // 臂部障碍：取创伤严重度与骨折严重度的较大值，使臂骨折即便软组织伤轻也能致残。
        float arm = Math.max(
                maxTraumaSeverity(data, BodyRegion.LEFT_ARM, BodyRegion.RIGHT_ARM),
                maxFractureSeverity(data, BodyRegion.LEFT_ARM, BodyRegion.RIGHT_ARM));
        if (arm > 0.3F) {
            symptoms.add(symptom("arm_impairment", arm, Optional.empty()));
        }

        // 疼痛：由全身总疼痛（含全局倍率与镇痛扣减）驱动；高疼痛额外引发颤抖，加重操作不稳与镜头摇晃。
        float totalPain = DefaultPhysiologyEngine.aggregateTotalPain(data, p);
        if (totalPain > 0.15F) {
            symptoms.add(symptom("pain", totalPain, Optional.empty()));
        }
        if (totalPain > 0.6F) {
            symptoms.add(symptom("tremor", Mth.clamp((totalPain - 0.6F) / 0.4F, 0.0F, 1.0F), Optional.empty()));
        }

        // 意识低下 → 昏迷症状（强度随意识降低上升），驱动游戏性输出的 unconscious 锁定。
        float consciousness = p.getConsciousness();
        if (consciousness < 0.3F) {
            symptoms.add(symptom("unconsciousness", Mth.clamp((0.3F - consciousness) / 0.3F, 0.0F, 1.0F), Optional.empty()));
        }

        // 体温异常 → 发热（>38.5℃）或寒战（<35.5℃），强度随偏离扩大。
        float coreTemp = p.getCoreTemperature();
        if (coreTemp > 38.5F) {
            symptoms.add(symptom("fever", Mth.clamp((coreTemp - 38.5F) / 3.0F, 0.0F, 1.0F), Optional.empty()));
        } else if (coreTemp < 35.5F) {
            symptoms.add(symptom("chills", Mth.clamp((35.5F - coreTemp) / 5.0F, 0.0F, 1.0F), Optional.empty()));
        }

        return new SymptomSnapshot(List.copyOf(symptoms), 0L);
    }

    private static SymptomState symptom(String path, float intensity, Optional<BodyRegion> region) {
        return new SymptomState(
                ResourceLocation.fromNamespaceAndPath("livingsystem", path),
                tier(intensity), Mth.clamp(intensity, 0.0F, 1.0F), region, Set.of());
    }

    private static SymptomTier tier(float intensity) {
        if (intensity >= 0.75F) {
            return SymptomTier.SEVERE;
        }
        if (intensity >= 0.5F) {
            return SymptomTier.MODERATE;
        }
        return SymptomTier.MILD;
    }

    private static float maxTraumaSeverity(PlayerHealthData data, BodyRegion... regions) {
        float max = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof TraumaInjuryState trauma) {
                for (BodyRegion region : regions) {
                    if (trauma.getBodyRegion() == region) {
                        max = Math.max(max, trauma.severity());
                    }
                }
            }
        }
        return max;
    }

    /** 取指定部位骨折的最大归一化严重度：等级（/3）占六成、不稳定占四成，无骨折返回 0。 */
    private static float maxFractureSeverity(PlayerHealthData data, BodyRegion... regions) {
        float max = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof TraumaInjuryState trauma && trauma.getFracture().getGrade() > 0) {
                for (BodyRegion region : regions) {
                    if (trauma.getBodyRegion() == region) {
                        float g = Mth.clamp(trauma.getFracture().getGrade() / 3.0F, 0.0F, 1.0F);
                        float severity = Mth.clamp(g * 0.6F + trauma.getFracture().getInstability() * 0.4F, 0.0F, 1.0F);
                        max = Math.max(max, severity);
                    }
                }
            }
        }
        return max;
    }
}
