package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * 污染组件状态。污染不等于感染：污染达到条件后才进行感染判定并创建单独的 {@code PathogenState}；
 * 通用创伤中不再保存 {@code infectionProgress}（见开发文档 §5.7）。
 */
public final class ContaminationState {

    /** 持久化 Codec。 */
    public static final Codec<ContaminationState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("contamination_level", 0.0F).forGetter(ContaminationState::getContaminationLevel),
            ResourceLocation.CODEC.optionalFieldOf("contaminant_id")
                    .forGetter(s -> Optional.ofNullable(s.getContaminantId())),
            Codec.FLOAT.optionalFieldOf("infection_risk", 0.0F).forGetter(ContaminationState::getInfectionRisk),
            Codec.BOOL.optionalFieldOf("cleaned", false).forGetter(ContaminationState::isCleaned)
    ).apply(instance, ContaminationState::fromCodec));

    /** 污染程度，0.0～1.0。 */
    private float contaminationLevel;
    /** 污染物 ID，可为空。 */
    private ResourceLocation contaminantId;
    /** 感染风险，0.0～1.0。 */
    private float infectionRisk;
    /** 是否已清洁。 */
    private boolean cleaned;

    public ContaminationState() {
    }

    private static ContaminationState fromCodec(float contaminationLevel, Optional<ResourceLocation> contaminantId,
                                                float infectionRisk, boolean cleaned) {
        ContaminationState state = new ContaminationState();
        state.contaminationLevel = contaminationLevel;
        state.contaminantId = contaminantId.orElse(null);
        state.infectionRisk = infectionRisk;
        state.cleaned = cleaned;
        return state;
    }

    public float getContaminationLevel() {
        return contaminationLevel;
    }

    public void setContaminationLevel(float contaminationLevel) {
        this.contaminationLevel = contaminationLevel;
    }

    public ResourceLocation getContaminantId() {
        return contaminantId;
    }

    public void setContaminantId(ResourceLocation contaminantId) {
        this.contaminantId = contaminantId;
    }

    public float getInfectionRisk() {
        return infectionRisk;
    }

    public void setInfectionRisk(float infectionRisk) {
        this.infectionRisk = infectionRisk;
    }

    public boolean isCleaned() {
        return cleaned;
    }

    public void setCleaned(boolean cleaned) {
        this.cleaned = cleaned;
    }
}
