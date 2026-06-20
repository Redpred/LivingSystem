package com.redpred.livingsystem.domain.effect;

/**
 * 疼痛组件状态。基础疼痛在创建时由伤势定义固化，当前疼痛由活动、炎症、镇痛等动态影响。
 */
public final class PainState {
    /** 基础疼痛，0.0～1.0。 */
    private float basePain;
    /** 当前疼痛贡献，0.0～1.0。 */
    private float currentPain;

    public PainState() {
    }

    public float getBasePain() {
        return basePain;
    }

    public void setBasePain(float basePain) {
        this.basePain = basePain;
    }

    public float getCurrentPain() {
        return currentPain;
    }

    public void setCurrentPain(float currentPain) {
        this.currentPain = currentPain;
    }
}
