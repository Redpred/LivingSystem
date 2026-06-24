package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * 异物组件状态。箭头、弹片、木刺等残留形成异物，必须使用支持的工具取出（见开发文档 §13.6.3）。
 */
public final class ForeignBodyState {

    /** 持久化 Codec。 */
    public static final Codec<ForeignBodyState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("present", false).forGetter(ForeignBodyState::isPresent),
            ResourceLocation.CODEC.optionalFieldOf("foreign_body_type")
                    .forGetter(s -> Optional.ofNullable(s.getForeignBodyType())),
            Codec.FLOAT.optionalFieldOf("depth", 0.0F).forGetter(ForeignBodyState::getDepth),
            Codec.BOOL.optionalFieldOf("vascular_compression", false).forGetter(ForeignBodyState::isVascularCompression)
    ).apply(instance, ForeignBodyState::fromCodec));

    /** 是否存在异物。 */
    private boolean present;
    /** 异物类型 ID，可为空。 */
    private ResourceLocation foreignBodyType;
    /** 异物深度，0.0～1.0。 */
    private float depth;
    /** 是否压迫血管（直接取出可能加重出血）。 */
    private boolean vascularCompression;

    public ForeignBodyState() {
    }

    private static ForeignBodyState fromCodec(boolean present, Optional<ResourceLocation> type,
                                              float depth, boolean vascularCompression) {
        ForeignBodyState state = new ForeignBodyState();
        state.present = present;
        state.foreignBodyType = type.orElse(null);
        state.depth = depth;
        state.vascularCompression = vascularCompression;
        return state;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public ResourceLocation getForeignBodyType() {
        return foreignBodyType;
    }

    public void setForeignBodyType(ResourceLocation foreignBodyType) {
        this.foreignBodyType = foreignBodyType;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public boolean isVascularCompression() {
        return vascularCompression;
    }

    public void setVascularCompression(boolean vascularCompression) {
        this.vascularCompression = vascularCompression;
    }
}
