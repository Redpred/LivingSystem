package com.redpred.livingsystem.domain.examination;

/**
 * 医疗信息等级（见开发文档 §15.1.1）。决定玩家能看到何种精度的健康信息。
 */
public enum MedicalInformationLevel {
    /** 玩家根据自身感受获得的信息。 */
    SUBJECTIVE,
    /** 玩家能够直接观察到的信息。 */
    OBSERVED,
    /** 使用基础设备测量得到的信息。 */
    MEASURED,
    /** 使用高级设备或检查手段获得的信息。 */
    DIAGNOSED,
    /** 仅管理员和调试工具可见的完整内部数据。 */
    DEBUG
}
