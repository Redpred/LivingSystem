package com.redpred.livingsystem.service;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * {@link StatusIconAggregator} 默认实现。阶段一无状态图标。
 */
public final class DefaultStatusIconAggregator implements StatusIconAggregator {

    @Override
    public List<ResourceLocation> aggregate(PlayerHealthData data) {
        return List.of();
    }
}
