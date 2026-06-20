package com.redpred.livingsystem.domain.effect;

import com.redpred.livingsystem.domain.body.BodyRegion;

import java.util.UUID;

/**
 * 热损伤（烧伤/烫伤/冻伤）的可变运行时状态（见开发文档 §5.8）。
 */
public final class ThermalInjuryState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private BodyRegion bodyRegion;
    private ThermalInjuryKind thermalKind;
    private float affectedArea;
    private float tissueDepth;
    private boolean ongoingExposure;
    private float fluidLossModifier;
    private float infectionRisk;

    public ThermalInjuryState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                              BodyRegion bodyRegion, ThermalInjuryKind thermalKind, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.bodyRegion = bodyRegion;
        this.thermalKind = thermalKind;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.THERMAL; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public BodyRegion getBodyRegion() { return bodyRegion; }
    public void setBodyRegion(BodyRegion bodyRegion) { this.bodyRegion = bodyRegion; }
    public ThermalInjuryKind getThermalKind() { return thermalKind; }
    public void setThermalKind(ThermalInjuryKind thermalKind) { this.thermalKind = thermalKind; }
    public float getAffectedArea() { return affectedArea; }
    public void setAffectedArea(float affectedArea) { this.affectedArea = affectedArea; }
    public float getTissueDepth() { return tissueDepth; }
    public void setTissueDepth(float tissueDepth) { this.tissueDepth = tissueDepth; }
    public boolean isOngoingExposure() { return ongoingExposure; }
    public void setOngoingExposure(boolean ongoingExposure) { this.ongoingExposure = ongoingExposure; }
    public float getFluidLossModifier() { return fluidLossModifier; }
    public void setFluidLossModifier(float fluidLossModifier) { this.fluidLossModifier = fluidLossModifier; }
    public float getInfectionRisk() { return infectionRisk; }
    public void setInfectionRisk(float infectionRisk) { this.infectionRisk = infectionRisk; }
}
