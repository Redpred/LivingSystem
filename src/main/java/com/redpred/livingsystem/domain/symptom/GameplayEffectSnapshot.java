package com.redpred.livingsystem.domain.symptom;

/**
 * 所有症状与局部功能最终汇总出的唯一游戏性输出（见开发文档 §9.6）。
 *
 * <p>所有倍率在汇总器中统一夹取上下限；属性修饰器使用固定 ID 更新数值，不每 tick 新增。不可变 {@code record}。</p>
 */
public record GameplayEffectSnapshot(
        float movementSpeedMultiplier,
        float jumpStrengthMultiplier,
        float attackSpeedMultiplier,
        float miningSpeedMultiplier,
        float mainHandStability,
        float offHandStability,
        boolean sprintAllowed,
        boolean jumpAllowed,
        float cameraSway,
        float vignetteIntensity,
        float blurIntensity,
        float breathingAudioIntensity,
        float heartbeatAudioIntensity,
        boolean unconscious
) {
    /** 无任何惩罚的中性输出。 */
    public static final GameplayEffectSnapshot NEUTRAL = new GameplayEffectSnapshot(
            1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, true, true,
            0.0F, 0.0F, 0.0F, 0.0F, 0.0F, false);
}
