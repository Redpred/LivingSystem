package com.redpred.livingsystem.domain.effect;

/**
 * 辐射类型（见开发文档 §5.8）。辐射主要累积剂量，达到阶段阈值后再产生影响。
 */
public enum RadiationType {
    ALPHA,
    BETA,
    GAMMA,
    NEUTRON
}
