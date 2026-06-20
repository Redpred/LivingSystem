package com.redpred.livingsystem.domain.treatment;

import net.minecraft.resources.ResourceLocation;

/**
 * 保存一个伤势当前已经接受的治疗（见开发文档 §13.15）。取代旧的 {@code bandaged/sutured/...}
 * 布尔字段：一个伤势持有若干 {@code AppliedTreatmentState}，按 {@link TreatmentSlot} 区分。
 */
public final class AppliedTreatmentState {
    /** 治疗定义 ID。 */
    private ResourceLocation treatmentId;
    /** 治疗占用的功能槽位。 */
    private TreatmentSlot slot;
    /** 当前有效强度，0.0～1.0。 */
    private float effectiveness;
    /** 治疗物品或处理的完整程度，0.0～1.0。 */
    private float integrity;
    /** 治疗应用时间。 */
    private long appliedGameTime;
    /** 剩余有效时间；永久状态使用特殊值（如 {@code -1}）。 */
    private long remainingDuration;
    /** 治疗是否仍然有效。 */
    private boolean active = true;

    public AppliedTreatmentState() {
    }

    public AppliedTreatmentState(ResourceLocation treatmentId, TreatmentSlot slot, long appliedGameTime) {
        this.treatmentId = treatmentId;
        this.slot = slot;
        this.appliedGameTime = appliedGameTime;
    }

    public ResourceLocation getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(ResourceLocation treatmentId) {
        this.treatmentId = treatmentId;
    }

    public TreatmentSlot getSlot() {
        return slot;
    }

    public void setSlot(TreatmentSlot slot) {
        this.slot = slot;
    }

    public float getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(float effectiveness) {
        this.effectiveness = effectiveness;
    }

    public float getIntegrity() {
        return integrity;
    }

    public void setIntegrity(float integrity) {
        this.integrity = integrity;
    }

    public long getAppliedGameTime() {
        return appliedGameTime;
    }

    public void setAppliedGameTime(long appliedGameTime) {
        this.appliedGameTime = appliedGameTime;
    }

    public long getRemainingDuration() {
        return remainingDuration;
    }

    public void setRemainingDuration(long remainingDuration) {
        this.remainingDuration = remainingDuration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
