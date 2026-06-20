package com.redpred.livingsystem.domain.symptom;

/**
 * 多来源影响同一玩家属性时的叠加规则（见开发文档 §3.9）。
 */
public enum StackingPolicy {
    MAX,
    MIN,
    SUM_CLAMPED,
    MULTIPLY,
    MOST_SEVERE,
    UNIQUE_BY_SOURCE
}
