package com.redpred.livingsystem.domain.body;

/**
 * 血管等级。用于描述血管结构与出血伤口涉及的血管口径，影响出血速率与止血难度。
 */
public enum VesselClass {
    /** 毛细血管。 */
    CAPILLARY,
    /** 小血管。 */
    SMALL,
    /** 中等血管。 */
    MEDIUM,
    /** 大血管。 */
    MAJOR
}
