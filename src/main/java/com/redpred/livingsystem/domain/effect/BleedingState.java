package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.body.VesselClass;
import com.redpred.livingsystem.rule.codec.EnumCodecs;

/**
 * 出血组件状态。出血速率属于具体伤势，不属于身体部位或血管结构；{@code baseExternalRate} 与
 * {@code baseInternalRate} 在创建时固化，不得在每次循环中被覆写（见开发文档 §5.6）。
 */
public final class BleedingState {

    /** 持久化 Codec。 */
    public static final Codec<BleedingState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("base_external_rate", 0.0F).forGetter(BleedingState::getBaseExternalRate),
            Codec.FLOAT.optionalFieldOf("base_internal_rate", 0.0F).forGetter(BleedingState::getBaseInternalRate),
            EnumCodecs.of(VesselClass.class).optionalFieldOf("vessel_class", VesselClass.CAPILLARY)
                    .forGetter(BleedingState::getVesselClass),
            Codec.FLOAT.optionalFieldOf("clot_progress", 0.0F).forGetter(BleedingState::getClotProgress),
            Codec.FLOAT.optionalFieldOf("clot_stability", 0.0F).forGetter(BleedingState::getClotStability),
            Codec.FLOAT.optionalFieldOf("rebleed_risk", 0.0F).forGetter(BleedingState::getRebleedRisk),
            Codec.BOOL.optionalFieldOf("arterial_pattern", false).forGetter(BleedingState::isArterialPattern),
            Codec.BOOL.optionalFieldOf("currently_bleeding", false).forGetter(BleedingState::isCurrentlyBleeding)
    ).apply(instance, BleedingState::fromCodec));

    /** 基础外出血速率（毫升/分钟）。 */
    private float baseExternalRate;
    /** 基础内出血速率（毫升/分钟）。 */
    private float baseInternalRate;
    /** 受累血管口径。 */
    private VesselClass vesselClass = VesselClass.CAPILLARY;
    /** 凝血进度，0.0～1.0。 */
    private float clotProgress;
    /** 凝块稳定度，0.0～1.0。 */
    private float clotStability;
    /** 再出血风险，0.0～1.0。 */
    private float rebleedRisk;
    /** 是否为动脉性出血模式。 */
    private boolean arterialPattern;
    /** 当前是否仍在出血。 */
    private boolean currentlyBleeding;

    public BleedingState() {
    }

    private static BleedingState fromCodec(float baseExternalRate, float baseInternalRate, VesselClass vesselClass,
                                           float clotProgress, float clotStability, float rebleedRisk,
                                           boolean arterialPattern, boolean currentlyBleeding) {
        BleedingState state = new BleedingState();
        state.baseExternalRate = baseExternalRate;
        state.baseInternalRate = baseInternalRate;
        state.vesselClass = vesselClass;
        state.clotProgress = clotProgress;
        state.clotStability = clotStability;
        state.rebleedRisk = rebleedRisk;
        state.arterialPattern = arterialPattern;
        state.currentlyBleeding = currentlyBleeding;
        return state;
    }

    public float getBaseExternalRate() {
        return baseExternalRate;
    }

    public void setBaseExternalRate(float baseExternalRate) {
        this.baseExternalRate = baseExternalRate;
    }

    public float getBaseInternalRate() {
        return baseInternalRate;
    }

    public void setBaseInternalRate(float baseInternalRate) {
        this.baseInternalRate = baseInternalRate;
    }

    public VesselClass getVesselClass() {
        return vesselClass;
    }

    public void setVesselClass(VesselClass vesselClass) {
        this.vesselClass = vesselClass;
    }

    public float getClotProgress() {
        return clotProgress;
    }

    public void setClotProgress(float clotProgress) {
        this.clotProgress = clotProgress;
    }

    public float getClotStability() {
        return clotStability;
    }

    public void setClotStability(float clotStability) {
        this.clotStability = clotStability;
    }

    public float getRebleedRisk() {
        return rebleedRisk;
    }

    public void setRebleedRisk(float rebleedRisk) {
        this.rebleedRisk = rebleedRisk;
    }

    public boolean isArterialPattern() {
        return arterialPattern;
    }

    public void setArterialPattern(boolean arterialPattern) {
        this.arterialPattern = arterialPattern;
    }

    public boolean isCurrentlyBleeding() {
        return currentlyBleeding;
    }

    public void setCurrentlyBleeding(boolean currentlyBleeding) {
        this.currentlyBleeding = currentlyBleeding;
    }
}
