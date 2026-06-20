package com.redpred.livingsystem.service.context;

import com.redpred.livingsystem.domain.exposure.ExposureCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 一次环境暴露采样的瞬时上下文（见开发文档 §18、§24.2）。瞬时输入对象，不持久化。
 */
public record ExposureContext(
        ServerPlayer player,
        ResourceLocation hazardId,
        ExposureCategory category,
        float intensity,
        long gameTime
) {
}
