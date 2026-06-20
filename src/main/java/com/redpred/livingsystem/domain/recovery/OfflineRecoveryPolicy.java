package com.redpred.livingsystem.domain.recovery;

/**
 * 玩家离线期间的恢复策略（见开发文档 §13.4.9）。无论哪种都必须设置最大模拟时间。
 */
public enum OfflineRecoveryPolicy {
    /** 离线期间完全停止恢复。 */
    PAUSED,
    /** 根据离线时间计算有限恢复。 */
    CLAMPED,
    /** 完整计算离线期间恢复。 */
    FULL
}
