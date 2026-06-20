package com.redpred.livingsystem.api.examination;

import net.minecraft.resources.ResourceLocation;

/**
 * 医疗检查相关公开 API（见开发文档 §30）。供其他模组注册检查设备与检查定义。阶段一为骨架接口。
 */
public interface ExaminationApi {

    /** 注册一个医疗检查定义（按其 ID）。 */
    void registerExamination(ResourceLocation examinationId);
}
