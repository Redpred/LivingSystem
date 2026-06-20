package com.redpred.livingsystem.domain.treatment;

/**
 * 治疗效果提交方式（见开发文档 §13.5.2）。
 */
public enum TreatmentCommitPolicy {
    /** 只有治疗完整完成后才产生效果（取异物、缝合、骨折复位）。 */
    ON_COMPLETE,
    /** 根据已完成进度产生同比例效果（清洗、消毒、吸氧、输液）。 */
    PROPORTIONAL,
    /** 达到不同阶段时逐步产生效果（包扎、手术）。 */
    STAGED
}
