package com.redpred.livingsystem.service.exposure;

import com.redpred.livingsystem.rule.definition.EnvironmentalHazardProfile;

import java.util.List;

/**
 * 环境危害定义注册表（见开发文档 §12、§23）。管理静态数据包定义与动态发射源提供的危害。
 *
 * <p>阶段四 4.2：暴露当前已启用的环境危害列表，供采样器按方块/流体/群系匹配。动态发射源在后续阶段接入。</p>
 */
public interface EnvironmentalHazardRegistry {

    /** 当前已注册的环境危害数量。 */
    int size();

    /** 当前已启用的环境危害定义列表。 */
    List<EnvironmentalHazardProfile> all();
}
