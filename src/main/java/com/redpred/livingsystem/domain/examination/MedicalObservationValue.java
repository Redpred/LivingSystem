package com.redpred.livingsystem.domain.examination;

/**
 * 一项医疗检查测量值（见开发文档 §15.1.3 的 {@code MedicalObservationValue}）。不可变 {@code record}。
 *
 * <p>注：开发文档引用该类型但未给字段，此处按“数值 + 中文展示文本”合理设计，便于不同精度展示
 * （未测量/估算范围/精确值）。</p>
 */
public record MedicalObservationValue(
        double numericValue,
        String displayZhCn
) {
}
