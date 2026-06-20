package com.redpred.livingsystem.service.physiology;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.physiology.ActivitySnapshot;
import com.redpred.livingsystem.domain.physiology.DerivedVitals;
import net.minecraft.server.level.ServerPlayer;

/**
 * 全身生理循环引擎（见开发文档 §6.3、§23）。按固定顺序执行出血、呼吸、循环、体温、毒素、病原体、
 * 辐射、组织修复与意识等更新，并计算派生生命体征。
 */
public interface PhysiologyEngine {

    /** 运行一次急性生理更新。 */
    void runCycle(ServerPlayer player, PlayerHealthData data, ActivitySnapshot activity);

    /** 根据底层状态计算派生生命体征。 */
    DerivedVitals computeVitals(PlayerHealthData data);
}
