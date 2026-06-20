package com.redpred.livingsystem.service;

/**
 * 健康模块性能记录（见开发文档 §34.4）。默认关闭详细性能日志。
 */
public interface HealthPerformanceProfiler {

    /** 性能日志是否启用。 */
    boolean enabled();

    /** 记录某模块一次计算的耗时（纳秒）。 */
    void record(String module, long durationNanos);
}
