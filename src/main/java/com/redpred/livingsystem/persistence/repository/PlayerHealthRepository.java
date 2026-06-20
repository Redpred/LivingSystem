package com.redpred.livingsystem.persistence.repository;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * 统一读写玩家健康聚合根（见开发文档 §23）。封装数据附件访问，是业务代码获取/保存
 * {@link PlayerHealthData} 的唯一入口。
 */
public interface PlayerHealthRepository {

    /** 获取玩家的健康数据。 */
    PlayerHealthData get(ServerPlayer player);

    /** 保存玩家的健康数据。 */
    void put(ServerPlayer player, PlayerHealthData data);
}
