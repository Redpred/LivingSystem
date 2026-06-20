package com.redpred.livingsystem.api.exposure;

import net.minecraft.resources.ResourceLocation;

/**
 * 环境暴露相关公开 API（见开发文档 §30、§12.8）。供其他模组注册动态环境危害发射源。
 * 阶段一为骨架接口。
 */
public interface ExposureApi {

    /** 注册一个动态环境危害发射源（按其 ID）。 */
    void registerHazardEmitter(ResourceLocation emitterId);
}
