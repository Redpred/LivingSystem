package com.redpred.livingsystem.service.symptom;

import com.redpred.livingsystem.domain.symptom.GameplayEffectSnapshot;
import com.redpred.livingsystem.domain.symptom.SymptomSnapshot;

/**
 * {@link GameplayEffectAggregator} 默认实现。阶段一返回无惩罚的中性输出。
 */
public final class DefaultGameplayEffectAggregator implements GameplayEffectAggregator {

    @Override
    public GameplayEffectSnapshot aggregate(SymptomSnapshot symptoms) {
        return GameplayEffectSnapshot.NEUTRAL;
    }
}
