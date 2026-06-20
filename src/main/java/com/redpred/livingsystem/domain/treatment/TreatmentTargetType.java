package com.redpred.livingsystem.domain.treatment;

/**
 * 治疗目标类型（见开发文档 §13.3）。
 */
public enum TreatmentTargetType {
    /** 作用于一个明确的局部伤势实例。 */
    INJURY_INSTANCE,
    /** 作用于一个非局部健康影响实例。 */
    HEALTH_EFFECT_INSTANCE,
    /** 作用于某个身体部位（仅辅助治疗）。 */
    BODY_REGION,
    /** 作用于玩家整体生理状态。 */
    SYSTEMIC
}
