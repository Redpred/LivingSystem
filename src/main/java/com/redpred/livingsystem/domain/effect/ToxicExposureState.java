package com.redpred.livingsystem.domain.effect;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 毒素暴露的可变运行时状态。毒素总负荷由全部活动实例汇总，不在全身生理状态中重复保存
 * （见开发文档 §5.8）。
 */
public final class ToxicExposureState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private ResourceLocation toxinId;
    private ExposureRoute route;
    private float absorbedAmount;
    private float unabsorbedAmount;
    private float metabolizedAmount;

    public ToxicExposureState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                              ResourceLocation toxinId, ExposureRoute route, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.toxinId = toxinId;
        this.route = route;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.TOXIC; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public ResourceLocation getToxinId() { return toxinId; }
    public void setToxinId(ResourceLocation toxinId) { this.toxinId = toxinId; }
    public ExposureRoute getRoute() { return route; }
    public void setRoute(ExposureRoute route) { this.route = route; }
    public float getAbsorbedAmount() { return absorbedAmount; }
    public void setAbsorbedAmount(float absorbedAmount) { this.absorbedAmount = absorbedAmount; }
    public float getUnabsorbedAmount() { return unabsorbedAmount; }
    public void setUnabsorbedAmount(float unabsorbedAmount) { this.unabsorbedAmount = unabsorbedAmount; }
    public float getMetabolizedAmount() { return metabolizedAmount; }
    public void setMetabolizedAmount(float metabolizedAmount) { this.metabolizedAmount = metabolizedAmount; }
}
