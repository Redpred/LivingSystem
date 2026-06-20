package com.redpred.livingsystem.service.feature;

import com.redpred.livingsystem.rule.snapshot.DisableExistingPolicy;
import com.redpred.livingsystem.rule.snapshot.FeatureKey;

/**
 * 统一的功能开关服务（见开发文档 §3.11）。其他服务只能通过本服务查询功能状态，不得在业务类中
 * 直接散布 {@code if (Config.enableXxx)} 判断。
 */
public interface FeatureGateService {

    /** 系统是否启用（任一维度启用即视为启用）。 */
    boolean isSystemEnabled(FeatureKey key);

    /** 是否允许创建新的伤势/异常/症状来源。 */
    boolean allowsGeneration(FeatureKey key);

    /** 已有状态是否继续模拟。 */
    boolean allowsSimulation(FeatureKey key);

    /** 是否影响实际玩法。 */
    boolean allowsGameplayEffects(FeatureKey key);

    /** 是否生成客户端表现。 */
    boolean allowsPresentation(FeatureKey key);

    /** 功能关闭后对已有状态的处理策略。 */
    DisableExistingPolicy disablePolicy(FeatureKey key);
}
