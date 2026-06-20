package com.redpred.livingsystem.service.symptom;

import com.redpred.livingsystem.domain.symptom.GameplayEffectSnapshot;
import com.redpred.livingsystem.domain.symptom.SymptomSnapshot;

/**
 * 游戏性效果汇总器（见开发文档 §9.6、§28）。把所有症状与局部功能统一汇总为唯一输出，
 * 在此统一夹取上下限，属性修饰器使用固定 ID 更新而非每 tick 新增。
 */
public interface GameplayEffectAggregator {

    /** 将症状快照汇总为唯一的游戏性输出。 */
    GameplayEffectSnapshot aggregate(SymptomSnapshot symptoms);
}
