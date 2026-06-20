package com.redpred.livingsystem.service.exposure;

import com.redpred.livingsystem.service.context.ExposureContext;

/**
 * 暴露累积服务（见开发文档 §12.6、§23）。合并同类暴露源并累积剂量，达到阈值后创建或更新健康影响，
 * 而非每次采样都创建新的健康影响实例。
 */
public interface ExposureAccumulatorService {

    /** 累积一次环境暴露。 */
    void accumulate(ExposureContext context);
}
