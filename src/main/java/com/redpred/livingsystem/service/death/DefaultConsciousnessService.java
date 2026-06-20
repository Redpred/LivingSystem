package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.death.ConsciousnessState;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link ConsciousnessService} 默认实现。阶段一恒为清醒。
 */
public final class DefaultConsciousnessService implements ConsciousnessService {

    @Override
    public ConsciousnessState evaluate(PlayerHealthData data) {
        return ConsciousnessState.ALERT;
    }

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        // 阶段一占位
    }
}
