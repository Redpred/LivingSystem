package com.redpred.livingsystem.service.scheduling;

/**
 * {@link HealthTickScheduler} 默认实现，按频道固定间隔取模判断。危重快速通道（间隔为 1）每 tick 运行。
 */
public final class DefaultHealthTickScheduler implements HealthTickScheduler {

    @Override
    public boolean shouldRun(TickChannel channel, long tickCount) {
        int interval = channel.interval();
        return interval <= 1 || tickCount % interval == 0;
    }
}
