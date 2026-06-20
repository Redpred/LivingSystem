package com.redpred.livingsystem.rule.snapshot;

/**
 * 功能关闭后对已有状态的处理策略（见开发文档 §3.10）。
 */
public enum DisableExistingPolicy {
    /** 保留已有数据，但停止发展、恢复和症状输出。 */
    KEEP_AND_FREEZE,
    /** 继续内部计算，但不产生游戏影响或客户端表现。 */
    KEEP_AND_SIMULATE_HIDDEN,
    /** 停止生成新状态，已有状态按恢复规则逐渐消失。 */
    RESOLVE_GRADUALLY,
    /** 立即删除已有状态。 */
    REMOVE_IMMEDIATELY
}
