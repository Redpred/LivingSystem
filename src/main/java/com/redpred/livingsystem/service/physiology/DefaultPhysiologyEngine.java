package com.redpred.livingsystem.service.physiology;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.physiology.ActivitySnapshot;
import com.redpred.livingsystem.domain.physiology.DerivedVitals;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link PhysiologyEngine} 默认实现。阶段一不运行任何循环，派生体征返回空值。
 */
public final class DefaultPhysiologyEngine implements PhysiologyEngine {

    @Override
    public void runCycle(ServerPlayer player, PlayerHealthData data, ActivitySnapshot activity) {
        // 阶段一占位
    }

    @Override
    public DerivedVitals computeVitals(PlayerHealthData data) {
        return DerivedVitals.EMPTY;
    }
}
