package com.redpred.livingsystem.domain.body;

/**
 * 空腔脏器专用状态。描述穿孔泄漏严重度与污染速率。
 */
public final class HollowOrgansState implements StructureSpecificState {
    /** 泄漏严重度，0.0～1.0。 */
    private float leakSeverity;
    /** 内容物污染速率。 */
    private float contaminationRate;

    public HollowOrgansState() {
    }

    public float getLeakSeverity() {
        return leakSeverity;
    }

    public void setLeakSeverity(float leakSeverity) {
        this.leakSeverity = leakSeverity;
    }

    public float getContaminationRate() {
        return contaminationRate;
    }

    public void setContaminationRate(float contaminationRate) {
        this.contaminationRate = contaminationRate;
    }
}
