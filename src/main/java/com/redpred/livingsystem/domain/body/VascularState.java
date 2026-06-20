package com.redpred.livingsystem.domain.body;

/**
 * 血管专用状态。描述该部位血管系统的主要口径等级。
 */
public final class VascularState implements StructureSpecificState {
    /** 该部位血管的主要口径等级。 */
    private VesselClass vesselClass = VesselClass.SMALL;

    public VascularState() {
    }

    public VesselClass getVesselClass() {
        return vesselClass;
    }

    public void setVesselClass(VesselClass vesselClass) {
        this.vesselClass = vesselClass;
    }
}
