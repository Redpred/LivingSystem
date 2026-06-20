package com.redpred.livingsystem.service.exposure;

/**
 * 环境危害定义注册表（见开发文档 §12、§23）。管理静态数据包定义与动态发射源提供的危害。
 *
 * <p>阶段一为骨架：环境危害定义类型在后续阶段建立后再充实查询接口。</p>
 */
public interface EnvironmentalHazardRegistry {

    /** 当前已注册的环境危害数量。 */
    int size();
}
