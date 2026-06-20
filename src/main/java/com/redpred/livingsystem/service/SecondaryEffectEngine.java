package com.redpred.livingsystem.service;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * 继发后果引擎（见开发文档 §6.7、§23）。根据当前状态创建或更新继发健康影响（如污染→感染、
 * 胸腔穿透→气胸），所有继发创建携带 parentEffectId 与 sourceEventId 并去重，避免每轮重复创建。
 */
public interface SecondaryEffectEngine {

    /** 处理一次继发后果计算。 */
    void process(ServerPlayer player, PlayerHealthData data);
}
