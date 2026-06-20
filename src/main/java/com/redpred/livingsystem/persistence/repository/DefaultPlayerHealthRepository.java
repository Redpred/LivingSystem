package com.redpred.livingsystem.persistence.repository;

import com.redpred.livingsystem.bootstrap.ModAttachments;
import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link PlayerHealthRepository} 默认实现，基于玩家数据附件。
 */
public final class DefaultPlayerHealthRepository implements PlayerHealthRepository {

    @Override
    public PlayerHealthData get(ServerPlayer player) {
        return player.getData(ModAttachments.PLAYER_HEALTH.get());
    }

    @Override
    public void put(ServerPlayer player, PlayerHealthData data) {
        player.setData(ModAttachments.PLAYER_HEALTH.get(), data);
    }
}
