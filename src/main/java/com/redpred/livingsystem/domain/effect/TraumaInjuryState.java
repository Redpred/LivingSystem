package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.treatment.AppliedTreatmentState;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.core.UUIDUtil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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

    /** 创伤的六个可变组件集合（仅用于持久化分组，规避顶层 Codec 字段上限）。 */
    public record Components(BleedingState bleeding, FractureState fracture, ForeignBodyState foreignBody,
                             ContaminationState contamination, PainState pain, HealingState healing) {
        public static final Codec<Components> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BleedingState.CODEC.fieldOf("bleeding").forGetter(Components::bleeding),
                FractureState.CODEC.fieldOf("fracture").forGetter(Components::fracture),
                ForeignBodyState.CODEC.fieldOf("foreign_body").forGetter(Components::foreignBody),
                ContaminationState.CODEC.fieldOf("contamination").forGetter(Components::contamination),
                PainState.CODEC.fieldOf("pain").forGetter(Components::pain),
                HealingState.CODEC.fieldOf("healing").forGetter(Components::healing)
        ).apply(instance, Components::new));
    }

    /** 持久化 Codec（六个组件经 {@link Components} 分组）。运行时引用（实体/伤害源）不持久化，仅存其标识快照。 */
    public static final Codec<TraumaInjuryState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(TraumaInjuryState::id),
            UUIDUtil.CODEC.fieldOf("source_event_id").forGetter(TraumaInjuryState::sourceEventId),
            CauseSnapshot.CODEC.optionalFieldOf("cause", CauseSnapshot.UNKNOWN).forGetter(TraumaInjuryState::cause),
            EnumCodecs.of(BodyRegion.class).fieldOf("body_region").forGetter(TraumaInjuryState::getBodyRegion),
            EnumCodecs.of(TraumaKind.class).fieldOf("trauma_kind").forGetter(TraumaInjuryState::getTraumaKind),
            Codec.FLOAT.optionalFieldOf("severity", 0.0F).forGetter(TraumaInjuryState::severity),
            Codec.FLOAT.optionalFieldOf("depth", 0.0F).forGetter(TraumaInjuryState::getDepth),
            Codec.FLOAT.optionalFieldOf("affected_area", 0.0F).forGetter(TraumaInjuryState::getAffectedArea),
            Codec.unboundedMap(EnumCodecs.of(AnatomicalStructure.class), Codec.FLOAT)
                    .optionalFieldOf("structure_damage", Map.of()).forGetter(t -> t.getStructureDamage()),
            Components.CODEC.fieldOf("components").forGetter(TraumaInjuryState::components),
            AppliedTreatmentState.CODEC.listOf().optionalFieldOf("applied_treatments", List.of())
                    .forGetter(TraumaInjuryState::getAppliedTreatments),
            Codec.LONG.fieldOf("created_game_time").forGetter(TraumaInjuryState::createdGameTime),
            Codec.LONG.optionalFieldOf("last_updated_game_time", 0L).forGetter(TraumaInjuryState::lastUpdatedGameTime),
            Codec.BOOL.optionalFieldOf("active", true).forGetter(TraumaInjuryState::active)
    ).apply(instance, TraumaInjuryState::fromCodec));

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

    /** 组装六个组件为持久化分组。 */
    public Components components() {
        return new Components(bleeding, fracture, foreignBody, contamination, pain, healing);
    }

    /** Codec 工厂：重建创伤实例并填充全部字段与组件。 */
    private static TraumaInjuryState fromCodec(UUID id, UUID sourceEventId, CauseSnapshot cause, BodyRegion region,
                                               TraumaKind kind, float severity, float depth, float affectedArea,
                                               Map<AnatomicalStructure, Float> structureDamage, Components components,
                                               List<AppliedTreatmentState> appliedTreatments, long createdGameTime,
                                               long lastUpdatedGameTime, boolean active) {
        TraumaInjuryState t = new TraumaInjuryState(id, sourceEventId, cause, region, kind, createdGameTime);
        t.severity = severity;
        t.depth = depth;
        t.affectedArea = affectedArea;
        t.structureDamage.putAll(structureDamage);
        t.bleeding = components.bleeding();
        t.fracture = components.fracture();
        t.foreignBody = components.foreignBody();
        t.contamination = components.contamination();
        t.pain = components.pain();
        t.healing = components.healing();
        t.appliedTreatments.addAll(appliedTreatments);
        t.lastUpdatedGameTime = lastUpdatedGameTime;
        t.active = active;
        return t;
    }
}
