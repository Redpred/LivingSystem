package com.redpred.livingsystem.domain.death;

/**
 * 意识状态（见开发文档 §14.6.2）。进入昏迷与恢复清醒使用不同阈值，避免反复切换。
 */
public enum ConsciousnessState {
    /** 正常清醒。 */
    ALERT,
    /** 意识轻度模糊。 */
    IMPAIRED,
    /** 接近失去意识。 */
    CRITICAL,
    /** 已经失去意识。 */
    UNCONSCIOUS,
    /** 已经满足死亡条件。 */
    TERMINAL
}
