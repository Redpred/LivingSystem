package com.redpred.livingsystem.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.body.StructureState;
import com.redpred.livingsystem.domain.death.DeathReportSnapshot;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import com.redpred.livingsystem.domain.exposure.ExposureAccumulator;
import com.redpred.livingsystem.domain.medication.MedicationEffectInstance;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.domain.symptom.GameplayEffectSnapshot;
import com.redpred.livingsystem.domain.treatment.AppliedTreatmentState;
import com.redpred.livingsystem.domain.treatment.TreatmentSession;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 玩家健康数据的唯一聚合根（见开发文档 §20、§17 全局不变量 1）。
 *
 * <p>本类持有全部权威运行时状态字段。<b>阶段二 2.1 持久化范围：</b>{@link #CODEC} 序列化版本字段、
 * 全身生理状态与各部位结构完整度（既成损伤）；活动健康影响（伤势）、专用结构状态（extra）等的完整
 * 序列化随后续子里程碑接入 {@code persistence.codec}。</p>
 */
public final class PlayerHealthData {

    /** 当前持久化结构版本。 */
    public static final int CURRENT_SCHEMA_VERSION = 1;

    private static final Codec<BodyRegion> BODY_REGION_CODEC =
            Codec.STRING.xmap(BodyRegion::valueOf, Enum::name);
    private static final Codec<AnatomicalStructure> STRUCTURE_CODEC =
            Codec.STRING.xmap(AnatomicalStructure::valueOf, Enum::name);
    private static final Codec<Map<BodyRegion, Map<AnatomicalStructure, Float>>> REGIONS_CODEC =
            Codec.unboundedMap(BODY_REGION_CODEC, Codec.unboundedMap(STRUCTURE_CODEC, Codec.FLOAT));

    public static final Codec<PlayerHealthData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("schemaVersion", CURRENT_SCHEMA_VERSION).forGetter(PlayerHealthData::getSchemaVersion),
            Codec.LONG.optionalFieldOf("rulesVersion", 0L).forGetter(PlayerHealthData::getRulesVersion),
            PhysiologyState.CODEC.optionalFieldOf("physiology").forGetter(data -> Optional.of(data.physiology)),
            REGIONS_CODEC.optionalFieldOf("body_regions", Map.of()).forGetter(PlayerHealthData::structureIntegrityMap),
            TraumaInjuryState.CODEC.listOf().optionalFieldOf("active_traumas", List.of())
                    .forGetter(PlayerHealthData::activeTraumaList)
    ).apply(instance, PlayerHealthData::fromCodec));

    private int schemaVersion;
    private long rulesVersion;

    private final PhysiologyState physiology = new PhysiologyState();
    private final EnumMap<BodyRegion, BodyRegionState> bodyRegions = new EnumMap<>(BodyRegion.class);
    private final Map<UUID, HealthEffectInstance> activeEffects = new HashMap<>();
    private final Map<UUID, AppliedTreatmentState> appliedTreatments = new HashMap<>();
    private final List<MedicationEffectInstance> medications = new ArrayList<>();
    private final Map<ResourceLocation, ExposureAccumulator> exposureAccumulators = new HashMap<>();
    private final Map<UUID, TreatmentSession> treatmentSessions = new HashMap<>();
    private final List<MedicalObservationSnapshot> observations = new ArrayList<>();
    private final List<DeathReportSnapshot> deathReports = new ArrayList<>();
    private final transient HealthDirtyFlags dirtyFlags = new HealthDirtyFlags();
    /** 最近一次汇总的游戏性输出（派生、瞬态，不持久化）。 */
    private transient GameplayEffectSnapshot gameplay = GameplayEffectSnapshot.NEUTRAL;

    public PlayerHealthData() {
        this(CURRENT_SCHEMA_VERSION, 0L);
    }

    public PlayerHealthData(int schemaVersion, long rulesVersion) {
        this.schemaVersion = schemaVersion;
        this.rulesVersion = rulesVersion;
        for (BodyRegion region : BodyRegion.VALUES) {
            bodyRegions.put(region, new BodyRegionState());
        }
    }

    public PlayerHealthData(PlayerHealthData other) {
        this(other.schemaVersion, other.rulesVersion);
        this.physiology.copyFrom(other.physiology);
        other.bodyRegions.forEach((region, state) ->
                state.getStructures().forEach((structure, ss) ->
                        bodyRegions.get(region).getOrCreateStructure(structure).setIntegrity(ss.getIntegrity())));
    }

    /** Codec 工厂：用既有默认实例承接版本/生理/结构完整度；其余运行时聚合保持默认。 */
    private static PlayerHealthData fromCodec(int schemaVersion, long rulesVersion,
                                              Optional<PhysiologyState> physiology,
                                              Map<BodyRegion, Map<AnatomicalStructure, Float>> regions,
                                              List<TraumaInjuryState> traumas) {
        PlayerHealthData data = new PlayerHealthData(schemaVersion, rulesVersion);
        physiology.ifPresent(p -> data.physiology.copyFrom(p));
        regions.forEach((region, structs) -> {
            BodyRegionState regionState = data.bodyRegions.get(region);
            if (regionState != null) {
                structs.forEach((structure, integrity) ->
                        regionState.getOrCreateStructure(structure).setIntegrity(integrity));
            }
        });
        for (TraumaInjuryState trauma : traumas) {
            data.activeEffects.put(trauma.id(), trauma);
            BodyRegionState regionState = data.bodyRegions.get(trauma.getBodyRegion());
            if (regionState != null) {
                regionState.getActiveEffectIds().add(trauma.id());
            }
        }
        return data;
    }

    /** 导出当前活动的机械创伤列表，供持久化（其它健康影响类型在对应系统接入时扩展为分发 Codec）。 */
    private List<TraumaInjuryState> activeTraumaList() {
        List<TraumaInjuryState> list = new ArrayList<>();
        for (HealthEffectInstance effect : activeEffects.values()) {
            if (effect instanceof TraumaInjuryState trauma) {
                list.add(trauma);
            }
        }
        return list;
    }

    /** 导出各部位已存在结构的完整度，供持久化。 */
    private Map<BodyRegion, Map<AnatomicalStructure, Float>> structureIntegrityMap() {
        Map<BodyRegion, Map<AnatomicalStructure, Float>> out = new EnumMap<>(BodyRegion.class);
        for (Map.Entry<BodyRegion, BodyRegionState> entry : bodyRegions.entrySet()) {
            EnumMap<AnatomicalStructure, StructureState> structs = entry.getValue().getStructures();
            if (!structs.isEmpty()) {
                Map<AnatomicalStructure, Float> map = new EnumMap<>(AnatomicalStructure.class);
                structs.forEach((structure, state) -> map.put(structure, state.getIntegrity()));
                out.put(entry.getKey(), map);
            }
        }
        return out;
    }

    public int getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(int schemaVersion) { this.schemaVersion = schemaVersion; }
    public long getRulesVersion() { return rulesVersion; }
    public void setRulesVersion(long rulesVersion) { this.rulesVersion = rulesVersion; }

    public PhysiologyState physiology() { return physiology; }
    public EnumMap<BodyRegion, BodyRegionState> bodyRegions() { return bodyRegions; }
    public Map<UUID, HealthEffectInstance> activeEffects() { return activeEffects; }
    public Map<UUID, AppliedTreatmentState> appliedTreatments() { return appliedTreatments; }
    public List<MedicationEffectInstance> medications() { return medications; }
    public Map<ResourceLocation, ExposureAccumulator> exposureAccumulators() { return exposureAccumulators; }
    public Map<UUID, TreatmentSession> treatmentSessions() { return treatmentSessions; }
    public List<MedicalObservationSnapshot> observations() { return observations; }
    public List<DeathReportSnapshot> deathReports() { return deathReports; }
    public HealthDirtyFlags dirtyFlags() { return dirtyFlags; }

    public GameplayEffectSnapshot gameplay() { return gameplay; }
    public void setGameplay(GameplayEffectSnapshot gameplay) { this.gameplay = gameplay; }
}
