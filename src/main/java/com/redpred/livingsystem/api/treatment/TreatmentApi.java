package com.redpred.livingsystem.api.treatment;

import com.redpred.livingsystem.rule.definition.TreatmentDefinition;

/**
 * 治疗相关公开 API（见开发文档 §30）。供其他模组注册治疗行为、药物与摄入物效果。阶段一为骨架接口。
 */
public interface TreatmentApi {

    /** 注册一个治疗行为定义。 */
    void registerTreatmentAction(TreatmentDefinition definition);
}
