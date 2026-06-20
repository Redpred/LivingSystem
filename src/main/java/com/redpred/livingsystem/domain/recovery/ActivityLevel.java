package com.redpred.livingsystem.domain.recovery;

/**
 * 玩家近期活动强度等级，用于恢复与恶化计算（见开发文档 §13.4.7）。
 */
public enum ActivityLevel {
    /** 睡眠、躺卧或长时间静止。 */
    RESTING,
    /** 普通站立和缓慢移动。 */
    LIGHT,
    /** 行走、普通挖掘和一般活动。 */
    MODERATE,
    /** 冲刺、跳跃、游泳和持续战斗。 */
    HEAVY,
    /** 长时间高强度活动。 */
    EXTREME
}
