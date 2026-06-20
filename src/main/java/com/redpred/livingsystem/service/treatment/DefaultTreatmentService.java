package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.domain.treatment.TreatmentSession;
import com.redpred.livingsystem.service.context.TreatmentContext;

import java.util.Optional;
import java.util.UUID;

/**
 * {@link TreatmentService} 默认实现。阶段一不创建任何治疗会话。
 */
public final class DefaultTreatmentService implements TreatmentService {

    @Override
    public Optional<TreatmentSession> startTreatment(TreatmentContext context) {
        return Optional.empty();
    }

    @Override
    public void advance(UUID sessionId) {
        // 阶段一占位
    }

    @Override
    public void cancel(UUID sessionId) {
        // 阶段一占位
    }
}
