package com.redpred.livingsystem.domain.effect;

import com.redpred.livingsystem.domain.body.BodyRegion;

import java.util.UUID;

/**
 * 电击损伤的可变运行时状态（见开发文档 §5.8）。
 */
public final class ElectricalInjuryState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private BodyRegion contactRegion;
    private float exposureDuration;
    private float rhythmDisturbance;
    private float nerveDisturbance;
    private float localBurn;

    public ElectricalInjuryState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                                 BodyRegion contactRegion, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.contactRegion = contactRegion;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.ELECTRICAL; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public BodyRegion getContactRegion() { return contactRegion; }
    public void setContactRegion(BodyRegion contactRegion) { this.contactRegion = contactRegion; }
    public float getExposureDuration() { return exposureDuration; }
    public void setExposureDuration(float exposureDuration) { this.exposureDuration = exposureDuration; }
    public float getRhythmDisturbance() { return rhythmDisturbance; }
    public void setRhythmDisturbance(float rhythmDisturbance) { this.rhythmDisturbance = rhythmDisturbance; }
    public float getNerveDisturbance() { return nerveDisturbance; }
    public void setNerveDisturbance(float nerveDisturbance) { this.nerveDisturbance = nerveDisturbance; }
    public float getLocalBurn() { return localBurn; }
    public void setLocalBurn(float localBurn) { this.localBurn = localBurn; }
}
