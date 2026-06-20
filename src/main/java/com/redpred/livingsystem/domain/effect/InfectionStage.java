package com.redpred.livingsystem.domain.effect;

/**
 * 感染阶段（见开发文档 §5.8）。
 */
public enum InfectionStage {
    EXPOSED,
    INCUBATING,
    SYMPTOMATIC,
    RECOVERING,
    CLEARED
}
