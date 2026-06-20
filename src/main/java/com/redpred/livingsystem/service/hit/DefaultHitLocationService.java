package com.redpred.livingsystem.service.hit;

import com.redpred.livingsystem.service.context.DamageContext;

/**
 * {@link HitLocationService} 默认实现。阶段一不做几何解析，恒返回全身作用。
 */
public final class DefaultHitLocationService implements HitLocationService {

    @Override
    public HitLocationResult resolve(DamageContext context) {
        return HitLocationResult.WHOLE_BODY;
    }
}
