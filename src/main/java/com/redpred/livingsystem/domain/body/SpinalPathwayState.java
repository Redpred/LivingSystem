package com.redpred.livingsystem.domain.body;

/**
 * 脊髓通路专用状态。分别描述运动与感觉信号的传导能力。
 */
public final class SpinalPathwayState implements StructureSpecificState {
    /** 运动信号传导能力，0.0～1.0。 */
    private float motorTransmission = 1.0F;
    /** 感觉信号传导能力，0.0～1.0。 */
    private float sensoryTransmission = 1.0F;

    public SpinalPathwayState() {
    }

    public float getMotorTransmission() {
        return motorTransmission;
    }

    public void setMotorTransmission(float motorTransmission) {
        this.motorTransmission = motorTransmission;
    }

    public float getSensoryTransmission() {
        return sensoryTransmission;
    }

    public void setSensoryTransmission(float sensoryTransmission) {
        this.sensoryTransmission = sensoryTransmission;
    }
}
