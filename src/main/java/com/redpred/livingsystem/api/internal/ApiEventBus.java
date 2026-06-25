package com.redpred.livingsystem.api.internal;

import com.redpred.livingsystem.domain.death.DeathReportSnapshot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 公开 API 事件分发（见开发文档 §30）。持有其他模组订阅的回调，由内部系统在事件发生时触发。
 * 监听器在逻辑主线程被调用；监听器中的异常被隔离，不影响主流程与其他监听器。
 */
public final class ApiEventBus {

    private static final List<Consumer<DeathReportSnapshot>> DEATH_REPORT_LISTENERS = new CopyOnWriteArrayList<>();

    private ApiEventBus() {
    }

    /** 订阅死亡报告生成事件。 */
    public static void addDeathReportListener(Consumer<DeathReportSnapshot> listener) {
        if (listener != null) {
            DEATH_REPORT_LISTENERS.add(listener);
        }
    }

    /** 触发死亡报告事件，逐一通知订阅者（异常隔离）。 */
    public static void fireDeathReport(DeathReportSnapshot report) {
        for (Consumer<DeathReportSnapshot> listener : DEATH_REPORT_LISTENERS) {
            try {
                listener.accept(report);
            } catch (Exception e) {
                com.redpred.livingsystem.LivingSystemMod.LOGGER.error("死亡报告事件监听器异常", e);
            }
        }
    }
}
