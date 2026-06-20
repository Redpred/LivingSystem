package com.redpred.livingsystem.domain.treatment;

/**
 * 治疗占用的功能槽位。同一伤势的同一槽位默认只能存在一个主要治疗状态（见开发文档 §13.15）。
 */
public enum TreatmentSlot {
    /** 覆盖伤口的绷带或敷料。 */
    WOUND_COVER,
    /** 加压、填塞或止血带等止血措施。 */
    HEMORRHAGE_CONTROL,
    /** 缝合、钉合或闭合贴。 */
    WOUND_CLOSURE,
    /** 夹板或固定支具。 */
    FRACTURE_STABILIZATION,
    /** 胸部密封或胸腔处理。 */
    CHEST_SUPPORT,
    /** 气道或供氧支持。 */
    RESPIRATORY_SUPPORT,
    /** 复杂手术修复状态。 */
    SURGICAL_REPAIR
}
