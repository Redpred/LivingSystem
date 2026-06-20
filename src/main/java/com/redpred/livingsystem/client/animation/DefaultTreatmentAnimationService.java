package com.redpred.livingsystem.client.animation;

import com.redpred.livingsystem.network.payload.TreatmentProgressPayload;

/**
 * {@link TreatmentAnimationService} 默认实现。阶段一不驱动任何动画。
 */
public final class DefaultTreatmentAnimationService implements TreatmentAnimationService {

    @Override
    public void update(TreatmentProgressPayload progress) {
        // 阶段一占位
    }
}
