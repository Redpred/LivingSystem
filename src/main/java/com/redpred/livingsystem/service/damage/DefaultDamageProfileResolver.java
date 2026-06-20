package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.rule.definition.DamageProfile;
import com.redpred.livingsystem.service.context.DamageContext;

import java.util.Optional;

/**
 * {@link DamageProfileResolver} 默认实现。阶段一无数据，恒返回空。
 */
public final class DefaultDamageProfileResolver implements DamageProfileResolver {

    @Override
    public Optional<DamageProfile> resolve(DamageContext context) {
        return Optional.empty();
    }
}
