package com.redpred.livingsystem.domain.effect;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 魔法异常的可变运行时状态（见开发文档 §5.2）。具体表现由数据定义，凋零等机制归入此类。
 */
public final class ArcaneConditionState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private ResourceLocation afflictionId;
    private float intensity;

    public ArcaneConditionState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                                ResourceLocation afflictionId, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.afflictionId = afflictionId;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.ARCANE; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public ResourceLocation getAfflictionId() { return afflictionId; }
    public void setAfflictionId(ResourceLocation afflictionId) { this.afflictionId = afflictionId; }
    public float getIntensity() { return intensity; }
    public void setIntensity(float intensity) { this.intensity = intensity; }
}
