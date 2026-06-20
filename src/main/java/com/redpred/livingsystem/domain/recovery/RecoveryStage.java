package com.redpred.livingsystem.domain.recovery;

/**
 * 伤势恢复阶段（见开发文档 §13.4.4）。
 */
public enum RecoveryStage {
    /** 仍在恶化或存在严重未处理问题。 */
    UNSTABLE,
    /** 已得到控制，但尚未明显修复。 */
    STABILIZED,
    /** 组织主要修复阶段。 */
    REPAIRING,
    /** 完成基础修复，功能逐渐恢复。 */
    REMODELING,
    /** 不再产生有效影响，允许归档或移除。 */
    RESOLVED
}
