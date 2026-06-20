package com.redpred.livingsystem.domain.effect;

/**
 * 健康影响分类。死亡或生理崩溃由终止规则判断，不属于健康影响分类，也不创建“终止伤口”
 * （见开发文档 §5.2）。
 */
public enum HealthEffectCategory {
    TRAUMA,
    THERMAL,
    ELECTRICAL,
    CHEMICAL,
    RESPIRATORY,
    TOXIC,
    PATHOGEN,
    RADIATION,
    METABOLIC,
    ARCANE
}
