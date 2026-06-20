package com.redpred.livingsystem.domain.medication;

import net.minecraft.resources.ResourceLocation;

/**
 * 一次已经进入玩家体内的药物剂量（见开发文档 §13.8）。
 *
 * <p>药物不在使用时直接修改最终状态，而是创建本实例并随药代阶段（吸收、起效、维持、衰减、代谢）
 * 持续变化。可变运行时状态。</p>
 */
public final class MedicationEffectInstance {
    /** 药物定义 ID。 */
    private final ResourceLocation medicationId;
    /** 给药途径。 */
    private MedicationRoute route;
    /** 本次使用剂量。 */
    private float dose;
    /** 尚未吸收的药物量。 */
    private float absorptionReservoir;
    /** 当前已吸收的药物量。 */
    private float absorbedAmount;
    /** 当前有效药物强度。 */
    private float activeStrength;
    /** 药物使用时间。 */
    private final long administeredGameTime;
    /** 药物是否仍然有效。 */
    private boolean active = true;

    public MedicationEffectInstance(ResourceLocation medicationId, MedicationRoute route,
                                    float dose, long administeredGameTime) {
        this.medicationId = medicationId;
        this.route = route;
        this.dose = dose;
        this.absorptionReservoir = dose;
        this.administeredGameTime = administeredGameTime;
    }

    public ResourceLocation getMedicationId() { return medicationId; }
    public MedicationRoute getRoute() { return route; }
    public void setRoute(MedicationRoute v) { this.route = v; }
    public float getDose() { return dose; }
    public void setDose(float v) { this.dose = v; }
    public float getAbsorptionReservoir() { return absorptionReservoir; }
    public void setAbsorptionReservoir(float v) { this.absorptionReservoir = v; }
    public float getAbsorbedAmount() { return absorbedAmount; }
    public void setAbsorbedAmount(float v) { this.absorbedAmount = v; }
    public float getActiveStrength() { return activeStrength; }
    public void setActiveStrength(float v) { this.activeStrength = v; }
    public long getAdministeredGameTime() { return administeredGameTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean v) { this.active = v; }
}
