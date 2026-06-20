package com.redpred.livingsystem.domain.body;

/**
 * 单个解剖结构的可变状态。完整度是唯一权威字段；损伤等级、是否失能、通用功能等均应动态计算，
 * 不在此重复持久化（见开发文档 §4.3）。
 */
public final class StructureState {
    /** 结构完整度，0.0～1.0，唯一权威完整度。 */
    private float integrity = 1.0F;
    /** 可选专用状态，按结构类型决定是否存在。 */
    private StructureSpecificState extra;

    public StructureState() {
    }

    public StructureState(StructureSpecificState extra) {
        this.extra = extra;
    }

    public float getIntegrity() {
        return integrity;
    }

    public void setIntegrity(float integrity) {
        this.integrity = Math.max(0.0F, Math.min(1.0F, integrity));
    }

    /** 损伤等级，等于 {@code 1 - integrity}，动态计算。 */
    public float damageLevel() {
        return 1.0F - integrity;
    }

    public StructureSpecificState getExtra() {
        return extra;
    }

    public void setExtra(StructureSpecificState extra) {
        this.extra = extra;
    }
}
