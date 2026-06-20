package com.redpred.livingsystem.api.event;

import com.redpred.livingsystem.domain.death.DeathReportSnapshot;

import java.util.function.Consumer;

/**
 * 事件订阅相关公开 API（见开发文档 §30）。供其他模组订阅伤势创建、治疗完成、昏迷与死亡报告等事件。
 * 阶段一仅提供死亡报告订阅骨架，其余事件后续阶段扩展。
 */
public interface EventApi {

    /** 订阅死亡报告生成事件。 */
    void onDeathReport(Consumer<DeathReportSnapshot> listener);
}
