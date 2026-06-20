package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.hit.HitLocationResult;

import java.util.List;

/**
 * {@link HealthEffectFactory} 默认实现。阶段一不创建任何健康影响，恒返回空列表。
 */
public final class DefaultHealthEffectFactory implements HealthEffectFactory {

    @Override
    public List<HealthEffectInstance> create(DamageContext context, HitLocationResult location) {
        return List.of();
    }
}
