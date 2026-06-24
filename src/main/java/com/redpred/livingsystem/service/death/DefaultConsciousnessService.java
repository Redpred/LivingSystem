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
        float c = data.physiology().getConsciousness();
        if (c < 0.05F) {
            return ConsciousnessState.UNCONSCIOUS;
        }
        if (c < 0.2F) {
            return ConsciousnessState.CRITICAL;
        }
        if (c < 0.5F) {
            return ConsciousnessState.IMPAIRED;
        }
        return ConsciousnessState.ALERT;
    }

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        // 意识数值由生理循环（PhysiologyEngine）按氧债与灌注维护；昏迷的游戏性锁定经症状→游戏性输出链施加。
        // 此处保留为扩展点（如昏迷专属计时、强制视角等），当前无需额外处理。
    }
}
