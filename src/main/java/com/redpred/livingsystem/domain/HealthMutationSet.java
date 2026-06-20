package com.redpred.livingsystem.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 事务式健康变更集（见开发文档 §29）。
 *
 * <p>一次健康更新先把所有变更收集到本集合，校验通过后再原子提交到 {@link PlayerHealthData}，
 * 避免任何子模块异常留下半写入状态。阶段一提供收集与提交骨架；数值/引用校验在后续阶段加入。</p>
 */
public final class HealthMutationSet {

    private final List<Consumer<PlayerHealthData>> mutations = new ArrayList<>();

    /** 追加一个变更操作。 */
    public HealthMutationSet add(Consumer<PlayerHealthData> mutation) {
        mutations.add(mutation);
        return this;
    }

    public boolean isEmpty() {
        return mutations.isEmpty();
    }

    /** 将全部变更原子提交到目标数据。 */
    public void commit(PlayerHealthData data) {
        for (Consumer<PlayerHealthData> mutation : mutations) {
            mutation.accept(data);
        }
    }
}
