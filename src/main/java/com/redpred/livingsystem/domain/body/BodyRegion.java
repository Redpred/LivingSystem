package com.redpred.livingsystem.domain.body;

/**
 * 玩家固定划分的七个身体部位。
 *
 * <p>身体部位本身不具有独立生命值，只用于归集结构状态、关联的健康影响与局部治疗状态。
 * 其中 {@link #HEAD_NECK} 在玩家界面中显示为“头部”。枚举顺序为持久化与网络的稳定序，
 * 不得随意调整。</p>
 */
public enum BodyRegion {
    /** 头颈部，界面显示为“头部”。 */
    HEAD_NECK,
    /** 胸腔。 */
    CHEST,
    /** 腹部。 */
    ABDOMEN,
    /** 左臂。 */
    LEFT_ARM,
    /** 右臂。 */
    RIGHT_ARM,
    /** 左腿。 */
    LEFT_LEG,
    /** 右腿。 */
    RIGHT_LEG;

    /** 缓存数组，避免每次调用 {@link #values()} 产生分配。 */
    public static final BodyRegion[] VALUES = values();

    /** 该部位名称的翻译键。 */
    public String translationKey() {
        return "livingsystem.body_region." + name().toLowerCase();
    }
}
