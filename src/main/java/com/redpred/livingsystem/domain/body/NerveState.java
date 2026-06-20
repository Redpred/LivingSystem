package com.redpred.livingsystem.domain.body;

/**
 * 神经专用状态。分别描述运动与感觉神经的完整度。
 */
public final class NerveState implements StructureSpecificState {
    /** 运动神经完整度，0.0～1.0。 */
    private float motorIntegrity = 1.0F;
    /** 感觉神经完整度，0.0～1.0。 */
    private float sensoryIntegrity = 1.0F;

    public NerveState() {
    }

    public float getMotorIntegrity() {
        return motorIntegrity;
    }

    public void setMotorIntegrity(float motorIntegrity) {
        this.motorIntegrity = motorIntegrity;
    }

    public float getSensoryIntegrity() {
        return sensoryIntegrity;
    }

    public void setSensoryIntegrity(float sensoryIntegrity) {
        this.sensoryIntegrity = sensoryIntegrity;
    }
}
