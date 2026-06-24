package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 愈合组件状态，附着于创伤伤势，记录其恢复进展。
 *
 * <p>恢复阶段枚举（{@code RecoveryStage}）与恢复速率计算位于 {@code recovery} 层，本组件只保存
 * 伤势自身可持续变化的愈合数据。</p>
 */
public final class HealingState {

    /** 持久化 Codec。 */
    public static final Codec<HealingState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("recoverable_integrity", 1.0F).forGetter(HealingState::getRecoverableIntegrity),
            Codec.FLOAT.optionalFieldOf("recovery_progress", 0.0F).forGetter(HealingState::getRecoveryProgress),
            Codec.BOOL.optionalFieldOf("stabilized", false).forGetter(HealingState::isStabilized)
    ).apply(instance, HealingState::fromCodec));

    /** 可恢复的完整度上限，0.0～1.0。 */
    private float recoverableIntegrity = 1.0F;
    /** 当前愈合进度，0.0～1.0。 */
    private float recoveryProgress;
    /** 是否已达到稳定（出血控制、异物取出、污染达标等）。 */
    private boolean stabilized;

    public HealingState() {
    }

    private static HealingState fromCodec(float recoverableIntegrity, float recoveryProgress, boolean stabilized) {
        HealingState state = new HealingState();
        state.recoverableIntegrity = recoverableIntegrity;
        state.recoveryProgress = recoveryProgress;
        state.stabilized = stabilized;
        return state;
    }

    public float getRecoverableIntegrity() {
        return recoverableIntegrity;
    }

    public void setRecoverableIntegrity(float recoverableIntegrity) {
        this.recoverableIntegrity = recoverableIntegrity;
    }

    public float getRecoveryProgress() {
        return recoveryProgress;
    }

    public void setRecoveryProgress(float recoveryProgress) {
        this.recoveryProgress = recoveryProgress;
    }

    public boolean isStabilized() {
        return stabilized;
    }

    public void setStabilized(boolean stabilized) {
        this.stabilized = stabilized;
    }
}
