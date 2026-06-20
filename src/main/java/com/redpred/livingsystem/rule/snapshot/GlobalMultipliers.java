package com.redpred.livingsystem.rule.snapshot;

/**
 * 服务端全局倍率（见开发文档 §3.1）。所有会影响实际游戏状态的全局缩放集中于此，避免在各模块
 * 重复进行全局修正。阶段一默认全部为 {@code 1.0}，具体平衡值由服务端配置控制。不可变 {@code record}。
 */
public record GlobalMultipliers(
        float injuryGenerationProbability,
        float structureDamage,
        float externalBleeding,
        float internalBleeding,
        float clotting,
        float painIntensity,
        float infectionProbability,
        float pathogenProgress,
        float toxinMetabolism,
        float radiationDose,
        float symptomPenalty,
        float treatmentEffect,
        float naturalRecovery
) {
    /** 全部为 1.0 的中性倍率。 */
    public static final GlobalMultipliers DEFAULT =
            new GlobalMultipliers(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
}
