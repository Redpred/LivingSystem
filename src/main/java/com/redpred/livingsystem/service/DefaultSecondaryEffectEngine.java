package com.redpred.livingsystem.service;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link SecondaryEffectEngine} 默认实现。阶段一不创建任何继发影响。
 */
public final class DefaultSecondaryEffectEngine implements SecondaryEffectEngine {

    @Override
    public void process(ServerPlayer player, PlayerHealthData data) {
        // 阶段一占位
    }
}
