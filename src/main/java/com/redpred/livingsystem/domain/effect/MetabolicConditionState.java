package com.redpred.livingsystem.domain.effect;

import java.util.UUID;

/**
 * 代谢异常的可变运行时状态（饥饿、脱水、力竭、干燥）。属于全身状态，不分配身体部位
 * （见开发文档 §5.8）。
 */
public final class MetabolicConditionState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private MetabolicConditionKind kind;

    public MetabolicConditionState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                                   MetabolicConditionKind kind, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.kind = kind;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.METABOLIC; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public MetabolicConditionKind getKind() { return kind; }
    public void setKind(MetabolicConditionKind kind) { this.kind = kind; }
}
