package com.redpred.livingsystem.domain.effect;

import net.minecraft.resources.ResourceLocation;

/**
 * 污染组件状态。污染不等于感染：污染达到条件后才进行感染判定并创建单独的 {@code PathogenState}；
 * 通用创伤中不再保存 {@code infectionProgress}（见开发文档 §5.7）。
 */
public final class ContaminationState {
    /** 污染程度，0.0～1.0。 */
    private float contaminationLevel;
    /** 污染物 ID，可为空。 */
    private ResourceLocation contaminantId;
    /** 感染风险，0.0～1.0。 */
    private float infectionRisk;
    /** 是否已清洁。 */
    private boolean cleaned;

    public ContaminationState() {
    }

    public float getContaminationLevel() {
        return contaminationLevel;
    }

    public void setContaminationLevel(float contaminationLevel) {
        this.contaminationLevel = contaminationLevel;
    }

    public ResourceLocation getContaminantId() {
        return contaminantId;
    }

    public void setContaminantId(ResourceLocation contaminantId) {
        this.contaminantId = contaminantId;
    }

    public float getInfectionRisk() {
        return infectionRisk;
    }

    public void setInfectionRisk(float infectionRisk) {
        this.infectionRisk = infectionRisk;
    }

    public boolean isCleaned() {
        return cleaned;
    }

    public void setCleaned(boolean cleaned) {
        this.cleaned = cleaned;
    }
}
