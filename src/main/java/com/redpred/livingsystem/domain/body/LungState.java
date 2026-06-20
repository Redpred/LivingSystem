package com.redpred.livingsystem.domain.body;

/**
 * 肺专用状态。描述漏气严重度与液体积聚。左右肺分别持有各自的实例。
 */
public final class LungState implements StructureSpecificState {
    /** 漏气严重度，0.0～1.0。 */
    private float airLeakSeverity;
    /** 肺内液体积聚程度，0.0～1.0。 */
    private float fluidAccumulation;

    public LungState() {
    }

    public float getAirLeakSeverity() {
        return airLeakSeverity;
    }

    public void setAirLeakSeverity(float airLeakSeverity) {
        this.airLeakSeverity = airLeakSeverity;
    }

    public float getFluidAccumulation() {
        return fluidAccumulation;
    }

    public void setFluidAccumulation(float fluidAccumulation) {
        this.fluidAccumulation = fluidAccumulation;
    }
}
