package com.redpred.livingsystem.service.recovery;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link RecoveryEngine} 默认实现。阶段一不进行任何恢复计算。
 */
public final class DefaultRecoveryEngine implements RecoveryEngine {

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        // 阶段一占位
    }
}
