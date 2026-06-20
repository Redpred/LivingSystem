package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link DeathConditionResolver} 默认实现。阶段一从不判定死亡。
 */
public final class DefaultDeathConditionResolver implements DeathConditionResolver {

    @Override
    public boolean shouldDie(ServerPlayer player, PlayerHealthData data) {
        return false;
    }
}
