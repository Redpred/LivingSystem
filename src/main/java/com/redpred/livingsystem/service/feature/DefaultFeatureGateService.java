package com.redpred.livingsystem.service.feature;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.rule.reload.RulesReloadManager;
import com.redpred.livingsystem.rule.snapshot.DisableExistingPolicy;
import com.redpred.livingsystem.rule.snapshot.FeatureKey;
import com.redpred.livingsystem.rule.snapshot.FeaturePolicy;

/**
 * {@link FeatureGateService} 的默认实现。
 *
 * <p>结合模组总开关与当前规则快照的功能策略解析结果：总开关关闭时一切判定为否；否则按各维度策略
 * 返回。阶段一的关闭策略统一返回默认值 {@link DisableExistingPolicy#KEEP_AND_FREEZE}，
 * 后续阶段从配置按键解析。</p>
 */
public final class DefaultFeatureGateService implements FeatureGateService {

    @Override
    public boolean isSystemEnabled(FeatureKey key) {
        if (!masterEnabled()) {
            return false;
        }
        FeaturePolicy policy = policy(key);
        return policy.generationEnabled() || policy.simulationEnabled()
                || policy.gameplayEffectsEnabled() || policy.presentationEnabled();
    }

    @Override
    public boolean allowsGeneration(FeatureKey key) {
        return masterEnabled() && policy(key).generationEnabled();
    }

    @Override
    public boolean allowsSimulation(FeatureKey key) {
        return masterEnabled() && policy(key).simulationEnabled();
    }

    @Override
    public boolean allowsGameplayEffects(FeatureKey key) {
        return masterEnabled() && policy(key).gameplayEffectsEnabled();
    }

    @Override
    public boolean allowsPresentation(FeatureKey key) {
        return masterEnabled() && policy(key).presentationEnabled();
    }

    @Override
    public DisableExistingPolicy disablePolicy(FeatureKey key) {
        return DisableExistingPolicy.KEEP_AND_FREEZE;
    }

    private static FeaturePolicy policy(FeatureKey key) {
        return RulesReloadManager.current().featurePolicies().resolve(key);
    }

    /** 读取模组总开关；配置尚未加载时安全回退为启用。 */
    private static boolean masterEnabled() {
        try {
            return ModConfigs.MASTER_ENABLED.get();
        } catch (RuntimeException e) {
            return true;
        }
    }
}
