package com.redpred.livingsystem.service;

/**
 * 医疗内容注册（见开发文档 §16、§23）。管理医疗物品与设备定义。
 *
 * <p>阶段一为骨架：物品/设备定义在内容阶段建立后再充实查询接口。</p>
 */
public interface MedicalContentRegistry {

    /** 当前已注册的医疗内容定义数量。 */
    int size();
}
