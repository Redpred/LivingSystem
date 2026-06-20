package com.redpred.livingsystem.service.symptom;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.symptom.SymptomSnapshot;
import net.minecraft.server.level.ServerPlayer;

/**
 * 症状引擎（见开发文档 §9、§23）。从权威健康状态单向只读地生成症状快照，不反向修改健康状态。
 */
public interface SymptomEngine {

    /** 根据当前健康状态计算症状快照。 */
    SymptomSnapshot computeSymptoms(ServerPlayer player, PlayerHealthData data);
}
