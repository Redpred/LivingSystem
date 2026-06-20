package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * 死亡判定服务（见开发文档 §14.7、§17 不变量 3）。是唯一的死亡判定入口，各伤口/毒素/器官模块
 * 不得独立调用玩家死亡。
 */
public interface DeathConditionResolver {

    /** 判断玩家当前是否满足死亡条件。 */
    boolean shouldDie(ServerPlayer player, PlayerHealthData data);
}
