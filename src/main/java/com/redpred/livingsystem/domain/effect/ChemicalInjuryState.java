package com.redpred.livingsystem.domain.effect;

import com.redpred.livingsystem.domain.body.BodyRegion;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 化学腐蚀损伤的可变运行时状态。必须独立建模，不复用热损伤（见开发文档 §5.8）。
 */
public final class ChemicalInjuryState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private float severity;
    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    private ResourceLocation corrosiveId;
    private BodyRegion contactRegion;
    private float erosionDepth;
    private float affectedArea;
    private float residualAmount;
    private float neutralizationProgress;
    private float respiratoryEffect;

    public ChemicalInjuryState(UUID id, UUID sourceEventId, CauseSnapshot cause,
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
    @Override public HealthEffectCategory category() { return HealthEffectCategory.CHEMICAL; }
    @Override public CauseSnapshot cause() { return cause; }
    @Override public float severity() { return severity; }
    @Override public long createdGameTime() { return createdGameTime; }
    @Override public long lastUpdatedGameTime() { return lastUpdatedGameTime; }
    @Override public boolean active() { return active; }

    public void setCause(CauseSnapshot cause) { this.cause = cause; }
    public void setSeverity(float severity) { this.severity = severity; }
    public void setLastUpdatedGameTime(long t) { this.lastUpdatedGameTime = t; }
    public void setActive(boolean active) { this.active = active; }

    public ResourceLocation getCorrosiveId() { return corrosiveId; }
    public void setCorrosiveId(ResourceLocation corrosiveId) { this.corrosiveId = corrosiveId; }
    public BodyRegion getContactRegion() { return contactRegion; }
    public void setContactRegion(BodyRegion contactRegion) { this.contactRegion = contactRegion; }
    public float getErosionDepth() { return erosionDepth; }
    public void setErosionDepth(float erosionDepth) { this.erosionDepth = erosionDepth; }
    public float getAffectedArea() { return affectedArea; }
    public void setAffectedArea(float affectedArea) { this.affectedArea = affectedArea; }
    public float getResidualAmount() { return residualAmount; }
    public void setResidualAmount(float residualAmount) { this.residualAmount = residualAmount; }
    public float getNeutralizationProgress() { return neutralizationProgress; }
    public void setNeutralizationProgress(float neutralizationProgress) { this.neutralizationProgress = neutralizationProgress; }
    public float getRespiratoryEffect() { return respiratoryEffect; }
    public void setRespiratoryEffect(float respiratoryEffect) { this.respiratoryEffect = respiratoryEffect; }
}
