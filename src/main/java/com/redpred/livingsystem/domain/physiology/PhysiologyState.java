package com.redpred.livingsystem.domain.physiology;

/**
 * 全身权威生理状态（见开发文档 §7.1）。
 *
 * <p>心脏、肺、气道、脑等器官功能从结构状态推导，不在此重复保存；总感染/毒素/疼痛/辐射负担
 * 由活动健康影响动态汇总。本类为可变运行时状态并持久化。阶段一的默认值为占位，平衡数值在后续
 * 阶段填充。</p>
 */
public final class PhysiologyState {
    /** 最大血液容量，默认值为5000毫升，由服务端配置控制。 */
    private float maxBloodVolume = 5000.0F;
    /** 当前血液容量，单位为毫升。 */
    private float currentBloodVolume = 5000.0F;
    /** 最大体力。 */
    private float maxStamina = 100.0F;
    /** 当前体力。 */
    private float currentStamina = 100.0F;
    /** 代谢能量储备。 */
    private float metabolicEnergy = 100.0F;
    /** 水分状态。 */
    private float hydration = 100.0F;
    /** 营养状态。 */
    private float nutrition = 100.0F;
    /** 核心体温，单位为摄氏度。 */
    private float coreTemperature = 37.0F;
    /** 呼吸储备。 */
    private float respiratoryReserve = 1.0F;
    /** 累计氧债。 */
    private float oxygenDebt = 0.0F;
    /** 意识值。 */
    private float consciousness = 1.0F;
    /** 基础凝血效率。 */
    private float baselineClottingEfficiency = 1.0F;
    /** 免疫储备。 */
    private float immuneReserve = 1.0F;
    /** 镇痛强度。 */
    private float analgesiaLevel = 0.0F;
    /** 镇静强度。 */
    private float sedationLevel = 0.0F;

    public PhysiologyState() {
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
