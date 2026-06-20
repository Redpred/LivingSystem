package com.redpred.livingsystem.domain.body;

/**
 * 骨骼专用状态。描述该部位骨骼的骨折与固定情况。
 */
public final class BoneState implements StructureSpecificState {
    /** 骨折等级，0 表示无骨折，数值越大越严重。 */
    private int fractureGrade;
    /** 不稳定程度，0.0～1.0。 */
    private float instability;
    /** 是否发生移位。 */
    private boolean displaced;
    /** 夹板或固定带来的稳定度，0.0～1.0。 */
    private float splintStability;

    public BoneState() {
    }

    public int getFractureGrade() {
        return fractureGrade;
    }

    public void setFractureGrade(int fractureGrade) {
        this.fractureGrade = fractureGrade;
    }

    public float getInstability() {
        return instability;
    }

    public void setInstability(float instability) {
        this.instability = instability;
    }

    public boolean isDisplaced() {
        return displaced;
    }

    public void setDisplaced(boolean displaced) {
        this.displaced = displaced;
    }

    public float getSplintStability() {
        return splintStability;
    }

    public void setSplintStability(float splintStability) {
        this.splintStability = splintStability;
    }
}
