package com.redpred.livingsystem.domain.exposure;

/**
 * 环境暴露触发方式（见开发文档 §12.3）。同一来源允许同时使用多个触发方式。
 */
public enum ExposureTriggerMode {
    /** 玩家接近暴露源。 */
    PROXIMITY,
    /** 玩家身体接触方块、流体或实体。 */
    CONTACT,
    /** 玩家身体部分或全部浸没。 */
    IMMERSION,
    /** 玩家眼部或呼吸位置处于危险介质中。 */
    INHALATION,
    /** 玩家食用或饮用污染物。 */
    INGESTION,
    /** 开放伤口接触污染环境。 */
    WOUND_CONTACT,
    /** 暴露源需要与玩家之间无遮挡。 */
    LINE_OF_SIGHT,
    /** 玩家位于指定群系、维度或区域内。 */
    AREA_OCCUPANCY
}
