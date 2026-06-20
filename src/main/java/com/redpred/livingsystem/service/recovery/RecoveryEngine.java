package com.redpred.livingsystem.service.recovery;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * 恢复引擎（见开发文档 §13.4、§23）。统一计算自然恢复与治疗增强恢复，按组织类型、伤势严重度、
 * 全身恢复能力、治疗状态、活动与并发症等推进伤势恢复阶段。
 */
public interface RecoveryEngine {

    /** 运行一次恢复更新。 */
    void tick(ServerPlayer player, PlayerHealthData data);
}
