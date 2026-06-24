package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 疼痛组件状态。基础疼痛在创建时由伤势定义固化，当前疼痛由活动、炎症、镇痛等动态影响。
 */
public final class PainState {

    /** 持久化 Codec（字段均带默认值，容旧档缺字段）。 */
    public static final Codec<PainState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("base_pain", 0.0F).forGetter(PainState::getBasePain),
            Codec.FLOAT.optionalFieldOf("current_pain", 0.0F).forGetter(PainState::getCurrentPain)
    ).apply(instance, PainState::fromCodec));

    /** 基础疼痛，0.0～1.0。 */
    private float basePain;
    /** 当前疼痛贡献，0.0～1.0。 */
    private float currentPain;

    public PainState() {
    }

    private static PainState fromCodec(float basePain, float currentPain) {
        PainState state = new PainState();
        state.basePain = basePain;
        state.currentPain = currentPain;
        return state;
    }

    public float getBasePain() {
        return basePain;
    }

    public void setBasePain(float basePain) {
        this.basePain = basePain;
    }

    public float getCurrentPain() {
        return currentPain;
    }

    public void setCurrentPain(float currentPain) {
        this.currentPain = currentPain;
    }
}
