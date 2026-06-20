package com.redpred.livingsystem.service.scheduling;

/**
 * 健康系统更新频道（见开发文档 §29.2）。把不同子系统分配到不同更新频率，避免每 tick 全量计算。
 *
 * <p>{@code SLOW}/{@code VERY_SLOW} 对应文档的 40～100 / 100～200 tick 区间（此处取代表间隔，
 * 具体值后续由服务端配置在安全范围内调整）；{@code CRITICAL_FAST} 为危重玩家快速通道。</p>
 */
public enum TickChannel {
    /** 每 1 tick：活动治疗会话、立即操作限制。 */
    EVERY_TICK(1),
    /** 每 5 tick：出血、呼吸储备、氧债、危重意识与死亡判定。 */
    EVERY_5_TICKS(5),
    /** 每 10 tick：体力、活动负荷、快速环境采样、过滤器消耗。 */
    EVERY_10_TICKS(10),
    /** 每 20 tick：体温、毒素、药物、生命体征、症状、常规环境采样。 */
    EVERY_20_TICKS(20),
    /** 慢速（病原体、辐射生物效应、装备污染、慢速代谢）。 */
    SLOW(60),
    /** 极慢（组织修复、骨折恢复、伤口愈合、恢复阶段）。 */
    VERY_SLOW(150),
    /** 危重快速通道。 */
    CRITICAL_FAST(1);

    private final int interval;

    TickChannel(int interval) {
        this.interval = interval;
    }

    /** 该频道的 tick 间隔。 */
    public int interval() {
        return interval;
    }
}
