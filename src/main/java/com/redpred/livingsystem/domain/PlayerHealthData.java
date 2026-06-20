package com.redpred.livingsystem.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.death.DeathReportSnapshot;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import com.redpred.livingsystem.domain.exposure.ExposureAccumulator;
import com.redpred.livingsystem.domain.medication.MedicationEffectInstance;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.domain.treatment.AppliedTreatmentState;
import com.redpred.livingsystem.domain.treatment.TreatmentSession;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家健康数据的唯一聚合根（见开发文档 §20、§17 全局不变量 1）。
 *
 * <p>禁止为血液、器官、伤势、症状、药物和治疗分别注册多个玩家附件——玩家只保存这一个聚合根。
 * 本类持有全部权威运行时状态字段。</p>
 *
 * <p><b>阶段一持久化范围：</b>{@link #CODEC} 目前仅序列化版本字段；{@link PhysiologyState}、各身体部位
 * 结构状态与活动健康影响集合等的完整（含多态健康影响）序列化将在后续阶段经 {@code persistence.codec}
 * 实现并配套 {@code schemaVersion} 升级与迁移器。当前阶段无实际健康数据产生，运行时聚合默认即可。</p>
 */
public final class PlayerHealthData {

    /** 当前持久化结构版本。 */
    public static final int CURRENT_SCHEMA_VERSION = 1;

    /** 持久化与读取使用的 Codec（阶段一仅版本字段）。 */
    public static final Codec<PlayerHealthData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("schemaVersion", CURRENT_SCHEMA_VERSION).forGetter(PlayerHealthData::getSchemaVersion),
            Codec.LONG.optionalFieldOf("rulesVersion", 0L).forGetter(PlayerHealthData::getRulesVersion)
    ).apply(instance, PlayerHealthData::new));

    private int schemaVersion;
    private long rulesVersion;

    /** 全身权威生理状态。 */
    private final PhysiologyState physiology = new PhysiologyState();
    /** 七个身体部位的结构状态。 */
    private final EnumMap<BodyRegion, BodyRegionState> bodyRegions = new EnumMap<>(BodyRegion.class);
    /** 全部活动健康影响。 */
    private final Map<UUID, HealthEffectInstance> activeEffects = new HashMap<>();
    /** 已应用治疗。 */
    private final Map<UUID, AppliedTreatmentState> appliedTreatments = new HashMap<>();
    /** 当前药物剂量实例。 */
    private final List<MedicationEffectInstance> medications = new ArrayList<>();
    /** 环境暴露累积器。 */
    private final Map<ResourceLocation, ExposureAccumulator> exposureAccumulators = new HashMap<>();
    /** 活动治疗会话。 */
    private final Map<UUID, TreatmentSession> treatmentSessions = new HashMap<>();
    /** 有限数量的医疗检查结果。 */
    private final List<MedicalObservationSnapshot> observations = new ArrayList<>();
    /** 有限数量的死亡报告。 */
    private final List<DeathReportSnapshot> deathReports = new ArrayList<>();
    /** 运行时脏标记，不持久化。 */
    private final transient HealthDirtyFlags dirtyFlags = new HealthDirtyFlags();

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

    /**
     * 拷贝构造。阶段一仅保留版本，运行时聚合（生理/伤势/治疗等）重置为默认；完整克隆与重生/维度切换
     * 策略在后续阶段实现。
     */
    public PlayerHealthData(PlayerHealthData other) {
        this(other.schemaVersion, other.rulesVersion);
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
}
