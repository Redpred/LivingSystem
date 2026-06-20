package com.redpred.livingsystem.domain.death;

/**
 * 死亡关键事件时间线中的一条记录（见开发文档 §14.8）。不可变 {@code record}。
 */
public record DeathTimelineEntry(
        long gameTime,
        String descriptionZhCn
) {
}
