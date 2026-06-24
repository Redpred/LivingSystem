package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.protection.ProtectionResult;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.hit.HitLocationResult;

import java.util.List;

/**
 * 根据伤害上下文与命中部位创建相应的 {@link HealthEffectInstance}（见开发文档 §23、§24.1）。
 */
public interface HealthEffectFactory {

    /**
     * 为本次伤害创建一个或多个健康影响实例（可能为空，例如全身非局部来源由其他引擎处理）。
     * {@code protection} 为已计算一次的 LivingSystem 专用防护结果，用于减免穿透严重度、结构损伤与伤口生成。
     */
    List<HealthEffectInstance> create(DamageContext context, HitLocationResult location, ProtectionResult protection);
}
