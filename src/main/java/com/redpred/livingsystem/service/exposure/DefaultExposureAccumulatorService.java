package com.redpred.livingsystem.service.exposure;

import com.redpred.livingsystem.service.context.ExposureContext;

/**
 * {@link ExposureAccumulatorService} 默认实现。阶段一不累积。
 */
public final class DefaultExposureAccumulatorService implements ExposureAccumulatorService {

    @Override
    public void accumulate(ExposureContext context) {
        // 阶段一占位
    }
}
