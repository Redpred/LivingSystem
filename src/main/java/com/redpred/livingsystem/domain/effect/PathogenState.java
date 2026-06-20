package com.redpred.livingsystem.domain.effect;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 病原体感染的可变运行时状态。病原体载量保存在各自实例中，全身感染负担动态汇总
 * （见开发文档 §5.8）。
 */
public final class PathogenState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private ResourceLocation pathogenId;
    private PathogenType pathogenType;
    private TransmissionRoute transmissionRoute;
    private InfectionStage stage = InfectionStage.EXPOSED;
    private float pathogenLoad;
    private float replicationRate;
    private float immuneControl;
    private float recoveryProgress;

    public PathogenState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                         ResourceLocation pathogenId, PathogenType pathogenType, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.pathogenId = pathogenId;
        this.pathogenType = pathogenType;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override public UUID id() { return id; }
    @Override public UUID sourceEventId() { return sourceEventId; }
    @Override public HealthEffectCategory category() { return HealthEffectCategory.PATHOGEN; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public ResourceLocation getPathogenId() { return pathogenId; }
    public void setPathogenId(ResourceLocation pathogenId) { this.pathogenId = pathogenId; }
    public PathogenType getPathogenType() { return pathogenType; }
    public void setPathogenType(PathogenType pathogenType) { this.pathogenType = pathogenType; }
    public TransmissionRoute getTransmissionRoute() { return transmissionRoute; }
    public void setTransmissionRoute(TransmissionRoute transmissionRoute) { this.transmissionRoute = transmissionRoute; }
    public InfectionStage getStage() { return stage; }
    public void setStage(InfectionStage stage) { this.stage = stage; }
    public float getPathogenLoad() { return pathogenLoad; }
    public void setPathogenLoad(float pathogenLoad) { this.pathogenLoad = pathogenLoad; }
    public float getReplicationRate() { return replicationRate; }
    public void setReplicationRate(float replicationRate) { this.replicationRate = replicationRate; }
    public float getImmuneControl() { return immuneControl; }
    public void setImmuneControl(float immuneControl) { this.immuneControl = immuneControl; }
    public float getRecoveryProgress() { return recoveryProgress; }
    public void setRecoveryProgress(float recoveryProgress) { this.recoveryProgress = recoveryProgress; }
}
