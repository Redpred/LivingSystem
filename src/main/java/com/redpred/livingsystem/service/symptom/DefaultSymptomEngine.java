package com.redpred.livingsystem.service.symptom;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.symptom.SymptomSnapshot;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link SymptomEngine} 默认实现。阶段一不产生症状，返回空快照。
 */
public final class DefaultSymptomEngine implements SymptomEngine {

    @Override
    public SymptomSnapshot computeSymptoms(ServerPlayer player, PlayerHealthData data) {
        return SymptomSnapshot.EMPTY;
    }
}
