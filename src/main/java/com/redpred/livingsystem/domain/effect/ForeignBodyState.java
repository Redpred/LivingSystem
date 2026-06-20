package com.redpred.livingsystem.domain.effect;

import net.minecraft.resources.ResourceLocation;

/**
 * 异物组件状态。箭头、弹片、木刺等残留形成异物，必须使用支持的工具取出（见开发文档 §13.6.3）。
 */
public final class ForeignBodyState {
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
