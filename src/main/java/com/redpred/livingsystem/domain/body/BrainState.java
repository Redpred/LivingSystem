package com.redpred.livingsystem.domain.body;

/**
 * 脑专用状态。描述神经功能完整度。
 */
public final class BrainState implements StructureSpecificState {
    /** 神经功能完整度，0.0～1.0。 */
    private float neurologicalIntegrity = 1.0F;

    public BrainState() {
    }

    public float getNeurologicalIntegrity() {
        return neurologicalIntegrity;
    }

    public void setNeurologicalIntegrity(float neurologicalIntegrity) {
        this.neurologicalIntegrity = neurologicalIntegrity;
    }
}
