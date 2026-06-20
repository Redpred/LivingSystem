package com.redpred.livingsystem.service;

/**
 * {@link HealthPerformanceProfiler} 默认实现。阶段一关闭，记录为空操作。
 */
public final class DefaultHealthPerformanceProfiler implements HealthPerformanceProfiler {

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public void record(String module, long durationNanos) {
        // 阶段一占位：默认不记录
    }
}
