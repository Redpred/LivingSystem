package com.redpred.livingsystem.domain.effect;

/**
 * 代谢异常类型（见开发文档 §5.8）。这些是全身状态，不随机分配身体部位。
 */
public enum MetabolicConditionKind {
    STARVATION,
    DEHYDRATION,
    EXHAUSTION,
    DESICCATION
}
