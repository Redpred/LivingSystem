package com.redpred.livingsystem.domain.effect;

import java.util.UUID;

/**
 * 呼吸异常的可变运行时状态（见开发文档 §5.8）。
 */
public final class RespiratoryInsultState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private RespiratoryInsultKind kind;
    private float airwayObstruction;
    private float oxygenDebtRate;
    private float lungStructureDamage;
    private float lungFluidLoad;
    private boolean ongoing;

    public RespiratoryInsultState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                                  RespiratoryInsultKind kind, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.kind = kind;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.RESPIRATORY; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public RespiratoryInsultKind getKind() { return kind; }
    public void setKind(RespiratoryInsultKind kind) { this.kind = kind; }
    public float getAirwayObstruction() { return airwayObstruction; }
    public void setAirwayObstruction(float airwayObstruction) { this.airwayObstruction = airwayObstruction; }
    public float getOxygenDebtRate() { return oxygenDebtRate; }
    public void setOxygenDebtRate(float oxygenDebtRate) { this.oxygenDebtRate = oxygenDebtRate; }
    public float getLungStructureDamage() { return lungStructureDamage; }
    public void setLungStructureDamage(float lungStructureDamage) { this.lungStructureDamage = lungStructureDamage; }
    public float getLungFluidLoad() { return lungFluidLoad; }
    public void setLungFluidLoad(float lungFluidLoad) { this.lungFluidLoad = lungFluidLoad; }
    public boolean isOngoing() { return ongoing; }
    public void setOngoing(boolean ongoing) { this.ongoing = ongoing; }
}
