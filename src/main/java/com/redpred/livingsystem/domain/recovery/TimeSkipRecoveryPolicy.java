package com.redpred.livingsystem.domain.recovery;

/**
 * 睡眠/时间跳过的恢复策略（见开发文档 §13.4.8）。默认 {@link #CLAMPED}，避免反复睡觉立即治愈重伤。
 */
public enum TimeSkipRecoveryPolicy {
    /** 跳过的时间不产生额外恢复。 */
    NONE,
    /** 根据配置上限计算有限恢复。 */
    CLAMPED,
    /** 完整模拟全部跳过时间。 */
    FULL
}
