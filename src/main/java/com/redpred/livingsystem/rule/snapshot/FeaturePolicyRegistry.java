package com.redpred.livingsystem.rule.snapshot;

import java.util.Map;

/**
 * 功能策略注册表，按 {@link FeatureKey} 保存功能策略，并支持由上到下的继承解析（见开发文档 §3.7）。
 *
 * <p>解析时若当前键未配置，向上回退到父级键，最终回退到 {@link FeaturePolicy#ALL_ON}。
 * 上级系统完全关闭时下级不得重新启用的约束，由 {@code FeatureGateService} 结合上级状态保证。</p>
 */
public final class FeaturePolicyRegistry {

    /** 空注册表（无任何覆盖，全部按默认解析）。 */
    public static final FeaturePolicyRegistry EMPTY = new FeaturePolicyRegistry(Map.of());

    private final Map<FeatureKey, FeaturePolicy> policies;

    public FeaturePolicyRegistry(Map<FeatureKey, FeaturePolicy> policies) {
        this.policies = Map.copyOf(policies);
    }

    /** 解析某功能键的有效策略：沿父级回退，最终回退 {@link FeaturePolicy#ALL_ON}。 */
    public FeaturePolicy resolve(FeatureKey key) {
        FeatureKey current = key;
        while (current != null) {
            FeaturePolicy policy = policies.get(current);
            if (policy != null) {
                return policy;
            }
            current = current.parent();
        }
        return FeaturePolicy.ALL_ON;
    }
}
