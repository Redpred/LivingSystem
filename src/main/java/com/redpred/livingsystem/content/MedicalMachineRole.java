package com.redpred.livingsystem.content;

/**
 * 医疗物资生产的抽象机器角色（见开发文档 §16.2.6、§31）。
 *
 * <p>生产配方抽象为机器角色，核心治疗逻辑不依赖特定机器：整合包可用 LivingSystem 自带机器、原版工作台
 * 或其他科技模组机器产出相同医疗物品。</p>
 */
public enum MedicalMachineRole {
    STERILIZER,
    DISTILLER,
    CENTRIFUGE,
    CHEMICAL_PROCESSOR,
    CULTURE_CHAMBER,
    PHARMACEUTICAL_ASSEMBLER,
    INJECTION_FILLER,
    REFRIGERATED_STORAGE
}
