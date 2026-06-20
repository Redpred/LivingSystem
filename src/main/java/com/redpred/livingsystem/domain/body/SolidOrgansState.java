package com.redpred.livingsystem.domain.body;

/**
 * 实质脏器专用状态。描述其对内出血的影响修正。
 */
public final class SolidOrgansState implements StructureSpecificState {
    /** 内出血修正，越高表示越容易发生内出血。 */
    private float internalBleedingModifier;

    public SolidOrgansState() {
    }

    public float getInternalBleedingModifier() {
        return internalBleedingModifier;
    }

    public void setInternalBleedingModifier(float internalBleedingModifier) {
        this.internalBleedingModifier = internalBleedingModifier;
    }
}
