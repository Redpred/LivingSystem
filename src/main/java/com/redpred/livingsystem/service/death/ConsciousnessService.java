package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.death.ConsciousnessState;
import net.minecraft.server.level.ServerPlayer;

/**
 * 意识服务（见开发文档 §14.6、§23）。处理意识状态、昏迷与苏醒，进入/恢复使用不同阈值避免反复切换。
 */
public interface ConsciousnessService {

    /** 根据当前生理状态评估意识状态。 */
    ConsciousnessState evaluate(PlayerHealthData data);

    /** 运行一次意识更新（昏迷限制、苏醒判定等）。 */
    void tick(ServerPlayer player, PlayerHealthData data);
}
