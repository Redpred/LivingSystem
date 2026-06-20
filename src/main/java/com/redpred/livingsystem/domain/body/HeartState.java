package com.redpred.livingsystem.domain.body;

/**
 * 心脏专用状态。描述心律稳定性。
 */
public final class HeartState implements StructureSpecificState {
    /** 心律稳定性，0.0～1.0。 */
    private float rhythmStability = 1.0F;

    public HeartState() {
    }

    public float getRhythmStability() {
        return rhythmStability;
    }

    public void setRhythmStability(float rhythmStability) {
        this.rhythmStability = rhythmStability;
    }
}
