package com.redpred.livingsystem.domain.exposure;

import net.minecraft.resources.ResourceLocation;

/**
 * 累积玩家对一种环境危害的接触量（见开发文档 §12.6）。
 *
 * <p>环境采样不应每次都创建新的健康影响实例，而是先累积到此处，达到阈值后再创建或更新健康影响。</p>
 */
public final class ExposureAccumulator {
    /** 暴露定义 ID。 */
    private final ResourceLocation hazardId;
    /** 当前暴露强度。 */
    private float currentIntensity;
    /** 累计暴露剂量。 */
    private float accumulatedDose;
    /** 上次接触该来源的时间。 */
    private long lastExposureGameTime;
    /** 当前是否仍处于暴露状态。 */
    private boolean active;

    public ExposureAccumulator(ResourceLocation hazardId) {
        this.hazardId = hazardId;
    }

    public ResourceLocation getHazardId() { return hazardId; }
    public float getCurrentIntensity() { return currentIntensity; }
    public void setCurrentIntensity(float v) { this.currentIntensity = v; }
    public float getAccumulatedDose() { return accumulatedDose; }
    public void setAccumulatedDose(float v) { this.accumulatedDose = v; }
    public long getLastExposureGameTime() { return lastExposureGameTime; }
    public void setLastExposureGameTime(long v) { this.lastExposureGameTime = v; }
    public boolean isActive() { return active; }
    public void setActive(boolean v) { this.active = v; }
}
