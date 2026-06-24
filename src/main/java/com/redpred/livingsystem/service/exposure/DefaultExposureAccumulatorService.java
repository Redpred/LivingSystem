package com.redpred.livingsystem.service.exposure;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.exposure.ExposureAccumulator;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.context.ExposureContext;

/**
 * {@link ExposureAccumulatorService} 默认实现（阶段四 4.1）。
 *
 * <p>把同一危害的多次采样合并到玩家聚合根中的 {@link ExposureAccumulator}：累积剂量、记录当前强度与
 * 最近接触时间。强度为 0 视为离开来源（标记非活动，由具体系统决定后续衰减/清除）。
 * 累积到的剂量供温度/呼吸（本阶段）与毒素/辐射（阶段五）等系统读取，不在采样时直接创建健康影响实例。</p>
 */
public final class DefaultExposureAccumulatorService implements ExposureAccumulatorService {

    @Override
    public void accumulate(ExposureContext context) {
        PlayerHealthData data = LivingServices.REPOSITORY.get(context.player());
        ExposureAccumulator accumulator = data.exposureAccumulators()
                .computeIfAbsent(context.hazardId(), ExposureAccumulator::new);
        accumulator.setCurrentIntensity(context.intensity());
        accumulator.setLastExposureGameTime(context.gameTime());
        accumulator.setActive(context.intensity() > 0.0F);
        if (context.intensity() > 0.0F) {
            accumulator.setAccumulatedDose(accumulator.getAccumulatedDose() + context.intensity());
        }
    }
}
