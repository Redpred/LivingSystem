package com.redpred.livingsystem.domain.physiology;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 全身权威生理状态（见开发文档 §7.1）。可变运行时状态并持久化（自阶段二 2.1 起经 {@link #CODEC}）。
 *
 * <p>器官功能从结构状态推导，不在此重复保存；总感染/毒素/疼痛/辐射负担由活动健康影响动态汇总。</p>
 */
public final class PhysiologyState {

    /** 持久化 Codec（全部字段带默认值，容旧档缺字段）。 */
    public static final Codec<PhysiologyState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("maxBloodVolume", 5000.0F).forGetter(PhysiologyState::getMaxBloodVolume),
            Codec.FLOAT.optionalFieldOf("currentBloodVolume", 5000.0F).forGetter(PhysiologyState::getCurrentBloodVolume),
            Codec.FLOAT.optionalFieldOf("maxStamina", 100.0F).forGetter(PhysiologyState::getMaxStamina),
            Codec.FLOAT.optionalFieldOf("currentStamina", 100.0F).forGetter(PhysiologyState::getCurrentStamina),
            Codec.FLOAT.optionalFieldOf("metabolicEnergy", 100.0F).forGetter(PhysiologyState::getMetabolicEnergy),
            Codec.FLOAT.optionalFieldOf("hydration", 100.0F).forGetter(PhysiologyState::getHydration),
            Codec.FLOAT.optionalFieldOf("nutrition", 100.0F).forGetter(PhysiologyState::getNutrition),
            Codec.FLOAT.optionalFieldOf("coreTemperature", 37.0F).forGetter(PhysiologyState::getCoreTemperature),
            Codec.FLOAT.optionalFieldOf("respiratoryReserve", 1.0F).forGetter(PhysiologyState::getRespiratoryReserve),
            Codec.FLOAT.optionalFieldOf("oxygenDebt", 0.0F).forGetter(PhysiologyState::getOxygenDebt),
            Codec.FLOAT.optionalFieldOf("consciousness", 1.0F).forGetter(PhysiologyState::getConsciousness),
            Codec.FLOAT.optionalFieldOf("baselineClottingEfficiency", 1.0F).forGetter(PhysiologyState::getBaselineClottingEfficiency),
            Codec.FLOAT.optionalFieldOf("immuneReserve", 1.0F).forGetter(PhysiologyState::getImmuneReserve),
            Codec.FLOAT.optionalFieldOf("analgesiaLevel", 0.0F).forGetter(PhysiologyState::getAnalgesiaLevel),
            Codec.FLOAT.optionalFieldOf("sedationLevel", 0.0F).forGetter(PhysiologyState::getSedationLevel)
    ).apply(instance, PhysiologyState::new));

    private float maxBloodVolume = 5000.0F;
    private float currentBloodVolume = 5000.0F;
    private float maxStamina = 100.0F;
    private float currentStamina = 100.0F;
    private float metabolicEnergy = 100.0F;
    private float hydration = 100.0F;
    private float nutrition = 100.0F;
    private float coreTemperature = 37.0F;
    private float respiratoryReserve = 1.0F;
    private float oxygenDebt = 0.0F;
    private float consciousness = 1.0F;
    private float baselineClottingEfficiency = 1.0F;
    private float immuneReserve = 1.0F;
    private float analgesiaLevel = 0.0F;
    private float sedationLevel = 0.0F;

    public PhysiologyState() {
    }

    public PhysiologyState(float maxBloodVolume, float currentBloodVolume, float maxStamina, float currentStamina,
                           float metabolicEnergy, float hydration, float nutrition, float coreTemperature,
                           float respiratoryReserve, float oxygenDebt, float consciousness,
                           float baselineClottingEfficiency, float immuneReserve, float analgesiaLevel,
                           float sedationLevel) {
        this.maxBloodVolume = maxBloodVolume;
        this.currentBloodVolume = currentBloodVolume;
        this.maxStamina = maxStamina;
        this.currentStamina = currentStamina;
        this.metabolicEnergy = metabolicEnergy;
        this.hydration = hydration;
        this.nutrition = nutrition;
        this.coreTemperature = coreTemperature;
        this.respiratoryReserve = respiratoryReserve;
        this.oxygenDebt = oxygenDebt;
        this.consciousness = consciousness;
        this.baselineClottingEfficiency = baselineClottingEfficiency;
        this.immuneReserve = immuneReserve;
        this.analgesiaLevel = analgesiaLevel;
        this.sedationLevel = sedationLevel;
    }

    /** 把另一实例的全部字段复制进来（用于反序列化填充既有实例）。 */
    public void copyFrom(PhysiologyState o) {
        this.maxBloodVolume = o.maxBloodVolume;
        this.currentBloodVolume = o.currentBloodVolume;
        this.maxStamina = o.maxStamina;
        this.currentStamina = o.currentStamina;
        this.metabolicEnergy = o.metabolicEnergy;
        this.hydration = o.hydration;
        this.nutrition = o.nutrition;
        this.coreTemperature = o.coreTemperature;
        this.respiratoryReserve = o.respiratoryReserve;
        this.oxygenDebt = o.oxygenDebt;
        this.consciousness = o.consciousness;
        this.baselineClottingEfficiency = o.baselineClottingEfficiency;
        this.immuneReserve = o.immuneReserve;
        this.analgesiaLevel = o.analgesiaLevel;
        this.sedationLevel = o.sedationLevel;
    }

    public float getMaxBloodVolume() { return maxBloodVolume; }
    public void setMaxBloodVolume(float v) { this.maxBloodVolume = v; }
    public float getCurrentBloodVolume() { return currentBloodVolume; }
    public void setCurrentBloodVolume(float v) { this.currentBloodVolume = v; }
    public float getMaxStamina() { return maxStamina; }
    public void setMaxStamina(float v) { this.maxStamina = v; }
    public float getCurrentStamina() { return currentStamina; }
    public void setCurrentStamina(float v) { this.currentStamina = v; }
    public float getMetabolicEnergy() { return metabolicEnergy; }
    public void setMetabolicEnergy(float v) { this.metabolicEnergy = v; }
    public float getHydration() { return hydration; }
    public void setHydration(float v) { this.hydration = v; }
    public float getNutrition() { return nutrition; }
    public void setNutrition(float v) { this.nutrition = v; }
    public float getCoreTemperature() { return coreTemperature; }
    public void setCoreTemperature(float v) { this.coreTemperature = v; }
    public float getRespiratoryReserve() { return respiratoryReserve; }
    public void setRespiratoryReserve(float v) { this.respiratoryReserve = v; }
    public float getOxygenDebt() { return oxygenDebt; }
    public void setOxygenDebt(float v) { this.oxygenDebt = v; }
    public float getConsciousness() { return consciousness; }
    public void setConsciousness(float v) { this.consciousness = v; }
    public float getBaselineClottingEfficiency() { return baselineClottingEfficiency; }
    public void setBaselineClottingEfficiency(float v) { this.baselineClottingEfficiency = v; }
    public float getImmuneReserve() { return immuneReserve; }
    public void setImmuneReserve(float v) { this.immuneReserve = v; }
    public float getAnalgesiaLevel() { return analgesiaLevel; }
    public void setAnalgesiaLevel(float v) { this.analgesiaLevel = v; }
    public float getSedationLevel() { return sedationLevel; }
    public void setSedationLevel(float v) { this.sedationLevel = v; }
}
