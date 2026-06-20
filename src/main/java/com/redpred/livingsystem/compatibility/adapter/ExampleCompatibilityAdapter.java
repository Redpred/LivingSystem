package com.redpred.livingsystem.compatibility.adapter;

import com.redpred.livingsystem.api.LivingSystemApi;
import com.redpred.livingsystem.api.LivingSystemApiHolder;

/**
 * 示例兼容适配器（见开发文档 §30、阶段任务 13）。演示其他模组如何通过公开 API 注册兼容数据。
 *
 * <p>阶段一仅为用法骨架，不实际注册任何内容（也容忍 API 尚未注入时为 {@code null}）。</p>
 */
public final class ExampleCompatibilityAdapter {

    private ExampleCompatibilityAdapter() {
    }

    public static void registerCompatibility() {
        LivingSystemApi api = LivingSystemApiHolder.get();
        if (api == null) {
            return;
        }
        // 示例（后续阶段填充具体定义）：
        // api.damage().registerDamageProfile(...);
        // api.protection().registerProtectionProfile(...);
        // api.treatment().registerTreatmentAction(...);
        // api.events().onDeathReport(report -> { ... });
    }
}
