package com.redpred.livingsystem.domain.effect;

/**
 * 愈合组件状态，附着于创伤伤势，记录其恢复进展。
 *
 * <p>恢复阶段枚举（{@code RecoveryStage}）与恢复速率计算位于 {@code recovery} 层，本组件只保存
 * 伤势自身可持续变化的愈合数据。</p>
 */
public final class HealingState {
    /** 可恢复的完整度上限，0.0～1.0。 */
    private float recoverableIntegrity = 1.0F;
    /** 当前愈合进度，0.0～1.0。 */
    private float recoveryProgress;
    /** 是否已达到稳定（出血控制、异物取出、污染达标等）。 */
    private boolean stabilized;

    public HealingState() {
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
