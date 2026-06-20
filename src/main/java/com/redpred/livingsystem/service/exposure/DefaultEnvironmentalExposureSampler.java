package com.redpred.livingsystem.service.exposure;

import net.minecraft.server.level.ServerPlayer;

/**
 * {@link EnvironmentalExposureSampler} 默认实现。阶段一不采样。
 */
public final class DefaultEnvironmentalExposureSampler implements EnvironmentalExposureSampler {

    @Override
    public void sample(ServerPlayer player) {
        // 阶段一占位
    }
}
