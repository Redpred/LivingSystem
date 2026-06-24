package com.redpred.livingsystem.service;

import com.redpred.livingsystem.persistence.repository.DefaultPlayerHealthRepository;
import com.redpred.livingsystem.persistence.repository.PlayerHealthRepository;
import com.redpred.livingsystem.service.damage.DamageContextFactory;
import com.redpred.livingsystem.service.damage.DefaultDamageContextFactory;
import com.redpred.livingsystem.service.damage.DefaultHealthEffectFactory;
import com.redpred.livingsystem.service.damage.HealthEffectFactory;
import com.redpred.livingsystem.service.death.DeathConditionResolver;
import com.redpred.livingsystem.service.death.DeathReportService;
import com.redpred.livingsystem.service.death.DefaultDeathConditionResolver;
import com.redpred.livingsystem.service.death.DefaultDeathReportService;
import com.redpred.livingsystem.service.examination.DefaultMedicalExaminationService;
import com.redpred.livingsystem.service.examination.DefaultMedicalObservationRegistry;
import com.redpred.livingsystem.service.examination.MedicalExaminationService;
import com.redpred.livingsystem.service.examination.MedicalObservationRegistry;
import com.redpred.livingsystem.service.exposure.DefaultEnvironmentalExposureSampler;
import com.redpred.livingsystem.service.exposure.DefaultEnvironmentalHazardRegistry;
import com.redpred.livingsystem.service.exposure.DefaultExposureAccumulatorService;
import com.redpred.livingsystem.service.exposure.EnvironmentalExposureSampler;
import com.redpred.livingsystem.service.exposure.EnvironmentalHazardRegistry;
import com.redpred.livingsystem.service.exposure.ExposureAccumulatorService;
import com.redpred.livingsystem.service.hit.DefaultHitLocationService;
import com.redpred.livingsystem.service.hit.HitLocationService;
import com.redpred.livingsystem.service.physiology.DefaultPhysiologyEngine;
import com.redpred.livingsystem.service.physiology.PhysiologyEngine;
import com.redpred.livingsystem.service.protection.DefaultProtectionResolver;
import com.redpred.livingsystem.service.protection.ProtectionResolver;
import com.redpred.livingsystem.service.recovery.DefaultRecoveryEngine;
import com.redpred.livingsystem.service.recovery.RecoveryEngine;
import com.redpred.livingsystem.service.resource.DefaultVanillaResourceBridge;
import com.redpred.livingsystem.service.resource.VanillaResourceBridge;
import com.redpred.livingsystem.service.structure.DefaultStructureDamageService;
import com.redpred.livingsystem.service.structure.StructureDamageService;
import com.redpred.livingsystem.service.symptom.DefaultGameplayEffectAggregator;
import com.redpred.livingsystem.service.symptom.DefaultSymptomEngine;
import com.redpred.livingsystem.service.symptom.GameplayEffectAggregator;
import com.redpred.livingsystem.service.symptom.SymptomEngine;
import com.redpred.livingsystem.service.treatment.ConsumableEffectService;
import com.redpred.livingsystem.service.treatment.DefaultConsumableEffectService;
import com.redpred.livingsystem.service.treatment.DefaultMedicationService;
import com.redpred.livingsystem.service.treatment.DefaultTreatmentService;
import com.redpred.livingsystem.service.treatment.MedicationService;
import com.redpred.livingsystem.service.treatment.TreatmentService;

/**
 * 简易服务定位器（阶段二）。持有各领域服务的默认实现单例，供事件接入层与桥接器编排调用。
 *
 * <p>阶段一服务均为接口 + 默认实现；此处集中实例化，避免在多个监听器各自 new。后续若引入更完整的
 * 依赖装配，可在此替换为可配置的提供方。</p>
 */
public final class LivingServices {

    public static final DamageContextFactory DAMAGE_CONTEXT = new DefaultDamageContextFactory();
    public static final HitLocationService HIT_LOCATION = new DefaultHitLocationService();
    public static final HealthEffectFactory HEALTH_EFFECT = new DefaultHealthEffectFactory();
    public static final StructureDamageService STRUCTURE = new DefaultStructureDamageService();
    public static final SymptomEngine SYMPTOM = new DefaultSymptomEngine();
    public static final GameplayEffectAggregator GAMEPLAY = new DefaultGameplayEffectAggregator();
    public static final PhysiologyEngine PHYSIOLOGY = new DefaultPhysiologyEngine();
    public static final DeathConditionResolver DEATH = new DefaultDeathConditionResolver();
    public static final DeathReportService DEATH_REPORT = new DefaultDeathReportService();
    public static final VanillaResourceBridge VANILLA_BRIDGE = new DefaultVanillaResourceBridge();
    public static final TreatmentService TREATMENT = new DefaultTreatmentService();
    public static final MedicationService MEDICATION = new DefaultMedicationService();
    public static final ConsumableEffectService CONSUMABLE = new DefaultConsumableEffectService();
    public static final RecoveryEngine RECOVERY = new DefaultRecoveryEngine();
    public static final MedicalExaminationService EXAMINATION = new DefaultMedicalExaminationService();
    public static final MedicalObservationRegistry OBSERVATIONS = new DefaultMedicalObservationRegistry();
    public static final ExposureAccumulatorService EXPOSURE = new DefaultExposureAccumulatorService();
    public static final EnvironmentalExposureSampler EXPOSURE_SAMPLER = new DefaultEnvironmentalExposureSampler();
    public static final EnvironmentalHazardRegistry HAZARDS = new DefaultEnvironmentalHazardRegistry();
    public static final ProtectionResolver PROTECTION = new DefaultProtectionResolver();
    public static final PlayerHealthRepository REPOSITORY = new DefaultPlayerHealthRepository();

    private LivingServices() {
    }
}
