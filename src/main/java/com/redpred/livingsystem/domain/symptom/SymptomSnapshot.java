package com.redpred.livingsystem.domain.symptom;

import java.util.List;

/**
 * 某一时刻的症状快照集合（见开发文档 §8.1）。默认不持久化，玩家重新登录后由权威健康状态重算。
 * 不可变 {@code record}。
 *
 * <p>注：开发文档提及 {@code SymptomSnapshot} 但未列出字段，此处按合理结构设计（症状列表 + 生成时间）。</p>
 */
public record SymptomSnapshot(
        List<SymptomState> symptoms,
        long gameTime
) {
    /** 空症状快照。 */
    public static final SymptomSnapshot EMPTY = new SymptomSnapshot(List.of(), 0L);
}
