package com.redpred.livingsystem.domain.treatment;

/**
 * 治疗目的（见开发文档 §13.4）。必须区分病因治疗与症状治疗。
 */
public enum TreatmentPurpose {
    /** 直接处理伤势或疾病原因。 */
    CAUSAL,
    /** 阻止患者状态继续恶化。 */
    STABILIZATION,
    /** 只减轻症状，不处理原因。 */
    SYMPTOMATIC,
    /** 支持全身生理状态。 */
    SUPPORTIVE,
    /** 促进后续恢复。 */
    RECOVERY
}
