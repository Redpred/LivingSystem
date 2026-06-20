package com.redpred.livingsystem.domain.medication;

/**
 * 给药途径（见开发文档 §13.8）。
 */
public enum MedicationRoute {
    /** 通过食物或药片口服。 */
    ORAL,
    /** 通过注射器注射。 */
    INJECTION,
    /** 通过静脉输液进入体内。 */
    INFUSION,
    /** 通过呼吸设备吸入。 */
    INHALATION,
    /** 涂抹在皮肤或伤口表面。 */
    TOPICAL
}
