package com.redpred.livingsystem.domain.recovery;

import net.minecraft.resources.ResourceLocation;

/**
 * 治疗状态对伤势恢复产生的修正（见开发文档 §13.4.5）。不可变 {@code record}。
 */
public record RecoveryModifier(
        ResourceLocation sourceId,
        float recoveryRateMultiplier,
        float recoverableIntegrityBonus,
        boolean providesStability,
        boolean preventsContamination,
        boolean reducesActivityAggravation
) {
}
