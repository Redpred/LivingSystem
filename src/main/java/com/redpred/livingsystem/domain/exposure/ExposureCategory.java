package com.redpred.livingsystem.domain.exposure;

/**
 * 环境暴露类别，决定累积后创建何种健康影响（见开发文档 §12.4 的 {@code exposureCategory}）。
 *
 * <p>注：开发文档引用了该类别但未列出具体取值，此处按文档涉及的环境危害种类合理设计。</p>
 */
public enum ExposureCategory {
    /** 高温环境。 */
    THERMAL_HEAT,
    /** 低温环境。 */
    THERMAL_COLD,
    /** 缺氧/呼吸危害（毒气、烟雾、低氧）。 */
    RESPIRATORY,
    /** 毒素暴露。 */
    TOXIN,
    /** 病原体暴露。 */
    PATHOGEN,
    /** 辐射暴露。 */
    RADIATION,
    /** 化学腐蚀暴露。 */
    CHEMICAL,
    /** 魔法异常环境。 */
    ARCANE
}
