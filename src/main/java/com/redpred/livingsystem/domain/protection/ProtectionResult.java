package com.redpred.livingsystem.domain.protection;

/**
 * 一次攻击或暴露经过防护后的统一结果（见开发文档 §11.8）。
 *
 * <p>只保存一次防护计算结果，后续各伤势模块读取该结果，不得分别重新计算装备防护。不可变 {@code record}。</p>
 */
public record ProtectionResult(
        float penetrationMultiplier,
        float structureDamageMultiplier,
        float affectedAreaMultiplier,
        float respiratoryPassThrough,
        float dermalPassThrough,
        float radiationPassThrough,
        float equipmentWear
) {
    /** 无防护（全部透过、无穿透衰减）的中性结果。 */
    public static final ProtectionResult NONE =
            new ProtectionResult(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F);
}
