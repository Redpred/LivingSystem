package com.redpred.livingsystem.rule.snapshot;

import org.jetbrains.annotations.Nullable;

/**
 * 功能开关键（见开发文档 §3.5 的四级层次）。
 *
 * <p>采用点分路径表示层级：模组总开关、系统级（如 {@code traumaSystem}）、类型级（如
 * {@code injury.cut_wound}）、输出级（如 {@code symptom.arm_impairment.attack_speed}）。
 * 下级配置可覆盖上级，但上级系统完全关闭时下级不得重新启用。</p>
 */
public record FeatureKey(String id) {

    public static FeatureKey of(String id) {
        return new FeatureKey(id);
    }

    /** 是否为顶层（系统级或总开关）键。 */
    public boolean isTopLevel() {
        return id.indexOf('.') < 0;
    }

    /** 上一级键；顶层键返回 {@code null}。 */
    @Nullable
    public FeatureKey parent() {
        int index = id.lastIndexOf('.');
        return index < 0 ? null : new FeatureKey(id.substring(0, index));
    }

    // —— 常用系统级键（见开发文档 §3.5.2）——
    public static final FeatureKey MASTER = of("master");
    public static final FeatureKey TRAUMA_SYSTEM = of("traumaSystem");
    public static final FeatureKey BLEEDING_SYSTEM = of("bleedingSystem");
    public static final FeatureKey PAIN_SYSTEM = of("painSystem");
    public static final FeatureKey FRACTURE_SYSTEM = of("fractureSystem");
    public static final FeatureKey RESPIRATORY_SYSTEM = of("respiratorySystem");
    public static final FeatureKey TEMPERATURE_SYSTEM = of("temperatureSystem");
    public static final FeatureKey TOXIN_SYSTEM = of("toxinSystem");
    public static final FeatureKey PATHOGEN_SYSTEM = of("pathogenSystem");
    public static final FeatureKey RADIATION_SYSTEM = of("radiationSystem");
    public static final FeatureKey METABOLIC_SYSTEM = of("metabolicSystem");
    public static final FeatureKey ARCANE_SYSTEM = of("arcaneSystem");
    public static final FeatureKey CONSCIOUSNESS_SYSTEM = of("consciousnessSystem");
    public static final FeatureKey SYMPTOM_SYSTEM = of("symptomSystem");
    public static final FeatureKey HEALING_SYSTEM = of("healingSystem");
    public static final FeatureKey TREATMENT_SYSTEM = of("treatmentSystem");
    public static final FeatureKey ENVIRONMENT_EXPOSURE_SYSTEM = of("environmentExposureSystem");
}
