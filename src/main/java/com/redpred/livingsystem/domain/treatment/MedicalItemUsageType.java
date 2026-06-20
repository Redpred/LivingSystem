package com.redpred.livingsystem.domain.treatment;

/**
 * 医疗物品的消耗方式（见开发文档 §13.14）。
 */
public enum MedicalItemUsageType {
    /** 使用一次后消耗一个物品。 */
    CONSUMABLE,
    /** 使用时消耗耐久。 */
    DURABILITY_TOOL,
    /** 需要同时消耗工具和材料。 */
    TOOL_AND_MATERIAL,
    /** 持续连接患者并消耗内部资源。 */
    CONTINUOUS_DEVICE
}
