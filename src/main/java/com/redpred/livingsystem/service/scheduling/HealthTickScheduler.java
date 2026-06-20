package com.redpred.livingsystem.service.scheduling;

/**
 * 健康调度器（见开发文档 §29、§23）。将各子系统分频运行，并对危重玩家提供快速通道，
 * 配合脏标记避免无变化重算。
 */
public interface HealthTickScheduler {

    /** 给定服务端 tick 计数，判断某频道本 tick 是否应运行。 */
    boolean shouldRun(TickChannel channel, long tickCount);
}
