package com.redpred.livingsystem.api;

import org.jetbrains.annotations.Nullable;

/**
 * 公开 API 实例持有者。模组初始化时由内部实现调用 {@link #set} 注入实现；其他模组通过 {@link #get}
 * 获取。阶段一尚未注入实现，{@link #get} 返回 {@code null}，调用方需判空。
 */
public final class LivingSystemApiHolder {

    private static volatile LivingSystemApi instance;

    private LivingSystemApiHolder() {
    }

    public static void set(LivingSystemApi api) {
        instance = api;
    }

    @Nullable
    public static LivingSystemApi get() {
        return instance;
    }
}
