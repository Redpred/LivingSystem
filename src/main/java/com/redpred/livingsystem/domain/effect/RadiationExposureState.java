package com.redpred.livingsystem.domain.effect;

import java.util.UUID;

/**
 * 辐射暴露的可变运行时状态。辐射主要累积剂量，达到阶段阈值后再产生影响（见开发文档 §5.8）。
 */
public final class RadiationExposureState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private RadiationType radiationType;
    private float accumulatedDose;
    private float externalContamination;
    private float internalContamination;

    public RadiationExposureState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                                  RadiationType radiationType, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.radiationType = radiationType;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.RADIATION; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public RadiationType getRadiationType() { return radiationType; }
    public void setRadiationType(RadiationType radiationType) { this.radiationType = radiationType; }
    public float getAccumulatedDose() { return accumulatedDose; }
    public void setAccumulatedDose(float accumulatedDose) { this.accumulatedDose = accumulatedDose; }
    public float getExternalContamination() { return externalContamination; }
    public void setExternalContamination(float externalContamination) { this.externalContamination = externalContamination; }
    public float getInternalContamination() { return internalContamination; }
    public void setInternalContamination(float internalContamination) { this.internalContamination = internalContamination; }
}
