package com.redpred.livingsystem.domain.recovery;

/**
 * 玩家当前可用于身体修复的综合恢复能力（见开发文档 §13.4.2）。是动态派生值，不作为独立资源消耗。
 */
public final class RecoveryCapacity {
    /** 综合恢复能力，0.0～1.0。 */
    private float totalCapacity;
    /** 代谢能量提供的修正。 */
    private float energyFactor;
    /** 水分状态提供的修正。 */
    private float hydrationFactor;
    /** 营养状态提供的修正。 */
    private float nutritionFactor;
    /** 氧合状态提供的修正。 */
    private float oxygenationFactor;
    /** 循环灌注提供的修正。 */
    private float perfusionFactor;
    /** 核心体温提供的修正。 */
    private float temperatureFactor;
    /** 休息和活动状态提供的修正。 */
    private float activityFactor;
    /** 感染、毒素和辐射造成的综合惩罚。 */
    private float complicationPenalty;

    public RecoveryCapacity() {
    }

    public float getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(float v) { this.totalCapacity = v; }
    public float getEnergyFactor() { return energyFactor; }
    public void setEnergyFactor(float v) { this.energyFactor = v; }
    public float getHydrationFactor() { return hydrationFactor; }
    public void setHydrationFactor(float v) { this.hydrationFactor = v; }
    public float getNutritionFactor() { return nutritionFactor; }
    public void setNutritionFactor(float v) { this.nutritionFactor = v; }
    public float getOxygenationFactor() { return oxygenationFactor; }
    public void setOxygenationFactor(float v) { this.oxygenationFactor = v; }
    public float getPerfusionFactor() { return perfusionFactor; }
    public void setPerfusionFactor(float v) { this.perfusionFactor = v; }
    public float getTemperatureFactor() { return temperatureFactor; }
    public void setTemperatureFactor(float v) { this.temperatureFactor = v; }
    public float getActivityFactor() { return activityFactor; }
    public void setActivityFactor(float v) { this.activityFactor = v; }
    public float getComplicationPenalty() { return complicationPenalty; }
    public void setComplicationPenalty(float v) { this.complicationPenalty = v; }
}
