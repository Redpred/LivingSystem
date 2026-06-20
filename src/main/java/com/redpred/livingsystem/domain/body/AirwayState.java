package com.redpred.livingsystem.domain.body;

/**
 * 呼吸道专用状态。描述阻塞与肿胀程度。上、下呼吸道分别持有各自的实例。
 */
public final class AirwayState implements StructureSpecificState {
    /** 气道阻塞程度，0.0～1.0。 */
    private float obstruction;
    /** 气道肿胀程度，0.0～1.0。 */
    private float swelling;

    public AirwayState() {
    }

    public float getObstruction() {
        return obstruction;
    }

    public void setObstruction(float obstruction) {
        this.obstruction = obstruction;
    }

    public float getSwelling() {
        return swelling;
    }

    public void setSwelling(float swelling) {
        this.swelling = swelling;
    }
}
