package com.redpred.livingsystem.domain.effect;

import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.treatment.AppliedTreatmentState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

/**
 * 局部创伤伤势的可变运行时状态（见开发文档 §5.5）。
 *
 * <p>结构累计完整度只能通过统一的结构损伤服务修改，禁止本类直接任意修改结构状态；
 * {@code structureDamage} 仅记录本伤势对各结构的损伤贡献。已接受的治疗按 §13.15 以
 * {@link AppliedTreatmentState} 列表保存（取代 §4.5 字面给出的单个 {@code TreatmentState} 字段，
 * 因为 §13.15 为更具体的强制要求）。</p>
 */
public final class TraumaInjuryState implements HealthEffectInstance {
    private final UUID id;
    private final UUID sourceEventId;
    private CauseSnapshot cause;
    private BodyRegion bodyRegion;
    private TraumaKind traumaKind;

    private float severity;
    private float depth;
    private float affectedArea;

    /** 本伤势对各结构造成的损伤贡献。 */
    private final EnumMap<AnatomicalStructure, Float> structureDamage = new EnumMap<>(AnatomicalStructure.class);

    private BleedingState bleeding = new BleedingState();
    private FractureState fracture = new FractureState();
    private ForeignBodyState foreignBody = new ForeignBodyState();
    private ContaminationState contamination = new ContaminationState();
    private PainState pain = new PainState();
    private final List<AppliedTreatmentState> appliedTreatments = new ArrayList<>();
    private HealingState healing = new HealingState();

    private final long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active = true;

    public TraumaInjuryState(UUID id, UUID sourceEventId, CauseSnapshot cause,
                             BodyRegion bodyRegion, TraumaKind traumaKind, long createdGameTime) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.cause = cause;
        this.bodyRegion = bodyRegion;
        this.traumaKind = traumaKind;
        this.createdGameTime = createdGameTime;
        this.lastUpdatedGameTime = createdGameTime;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public UUID sourceEventId() {
        return sourceEventId;
    }

    @Override
    public HealthEffectCategory category() {
        return HealthEffectCategory.TRAUMA;
    }

    @Override
    public CauseSnapshot cause() {
        return cause;
    }

    @Override
    public float severity() {
        return severity;
    }

    @Override
    public long createdGameTime() {
        return createdGameTime;
    }

    @Override
    public long lastUpdatedGameTime() {
        return lastUpdatedGameTime;
    }

    @Override
    public boolean active() {
        return active;
    }

    public BodyRegion getBodyRegion() {
        return bodyRegion;
    }

    public void setBodyRegion(BodyRegion bodyRegion) {
        this.bodyRegion = bodyRegion;
    }

    public TraumaKind getTraumaKind() {
        return traumaKind;
    }

    public void setTraumaKind(TraumaKind traumaKind) {
        this.traumaKind = traumaKind;
    }

    public void setSeverity(float severity) {
        this.severity = severity;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(float affectedArea) {
        this.affectedArea = affectedArea;
    }

    public EnumMap<AnatomicalStructure, Float> getStructureDamage() {
        return structureDamage;
    }

    public BleedingState getBleeding() {
        return bleeding;
    }

    public void setBleeding(BleedingState bleeding) {
        this.bleeding = bleeding;
    }

    public FractureState getFracture() {
        return fracture;
    }

    public void setFracture(FractureState fracture) {
        this.fracture = fracture;
    }

    public ForeignBodyState getForeignBody() {
        return foreignBody;
    }

    public void setForeignBody(ForeignBodyState foreignBody) {
        this.foreignBody = foreignBody;
    }

    public ContaminationState getContamination() {
        return contamination;
    }

    public void setContamination(ContaminationState contamination) {
        this.contamination = contamination;
    }

    public PainState getPain() {
        return pain;
    }

    public void setPain(PainState pain) {
        this.pain = pain;
    }

    public List<AppliedTreatmentState> getAppliedTreatments() {
        return appliedTreatments;
    }

    public HealingState getHealing() {
        return healing;
    }

    public void setHealing(HealingState healing) {
        this.healing = healing;
    }

    public void setCause(CauseSnapshot cause) {
        this.cause = cause;
    }

    public void setLastUpdatedGameTime(long lastUpdatedGameTime) {
        this.lastUpdatedGameTime = lastUpdatedGameTime;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
