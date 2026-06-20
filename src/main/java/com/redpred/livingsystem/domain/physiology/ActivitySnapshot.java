package com.redpred.livingsystem.domain.physiology;

/**
 * 玩家近期活动快照（见开发文档 §8.4）。玩家活动不是症状，而是下一轮生理计算的输入。不可变 {@code record}。
 */
public record ActivitySnapshot(
        boolean resting,
        boolean walking,
        boolean sprinting,
        boolean jumping,
        boolean swimming,
        boolean usingMainHand,
        boolean usingOffHand,
        float recentMovementIntensity
) {
    /** 静止占位快照。 */
    public static final ActivitySnapshot RESTING =
            new ActivitySnapshot(true, false, false, false, false, false, false, 0.0F);
}
