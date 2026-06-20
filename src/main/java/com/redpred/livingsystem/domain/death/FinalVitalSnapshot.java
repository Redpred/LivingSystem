package com.redpred.livingsystem.domain.death;

/**
 * 死亡前的最终生命体征摘要（见开发文档 §14.8）。不可变 {@code record}。
 *
 * <p>注：开发文档引用 {@code finalVitals} 但未给字段，此处按死亡报告需要展示的关键体征合理设计。</p>
 */
public record FinalVitalSnapshot(
        float bloodVolumeFraction,
        float consciousness,
        float perfusionIndex,
        float oxygenSaturation,
        float coreTemperature
) {
}
