package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 骨折组件状态，附着于创伤伤势。结构层 {@code BoneState} 描述部位骨骼整体情况，本组件描述
 * 该具体伤势造成的骨折细节。
 */
public final class FractureState {

    /** 持久化 Codec。 */
    public static final Codec<FractureState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("grade", 0).forGetter(FractureState::getGrade),
            Codec.FLOAT.optionalFieldOf("instability", 0.0F).forGetter(FractureState::getInstability),
            Codec.BOOL.optionalFieldOf("displaced", false).forGetter(FractureState::isDisplaced),
            Codec.FLOAT.optionalFieldOf("splint_stability", 0.0F).forGetter(FractureState::getSplintStability),
            Codec.BOOL.optionalFieldOf("reduced", false).forGetter(FractureState::isReduced)
    ).apply(instance, FractureState::fromCodec));

    /** 骨折等级，0 表示无骨折。 */
    private int grade;
    /** 不稳定程度，0.0～1.0。 */
    private float instability;
    /** 是否移位。 */
    private boolean displaced;
    /** 夹板/固定带来的稳定度，0.0～1.0。 */
    private float splintStability;
    /** 是否已复位。 */
    private boolean reduced;

    public FractureState() {
    }

    private static FractureState fromCodec(int grade, float instability, boolean displaced,
                                           float splintStability, boolean reduced) {
        FractureState state = new FractureState();
        state.grade = grade;
        state.instability = instability;
        state.displaced = displaced;
        state.splintStability = splintStability;
        state.reduced = reduced;
        return state;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public float getInstability() {
        return instability;
    }

    public void setInstability(float instability) {
        this.instability = instability;
    }

    public boolean isDisplaced() {
        return displaced;
    }

    public void setDisplaced(boolean displaced) {
        this.displaced = displaced;
    }

    public float getSplintStability() {
        return splintStability;
    }

    public void setSplintStability(float splintStability) {
        this.splintStability = splintStability;
    }

    public boolean isReduced() {
        return reduced;
    }

    public void setReduced(boolean reduced) {
        this.reduced = reduced;
    }
}
