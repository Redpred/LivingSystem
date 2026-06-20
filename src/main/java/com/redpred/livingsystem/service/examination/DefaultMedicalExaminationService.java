package com.redpred.livingsystem.service.examination;

import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * {@link MedicalExaminationService} 默认实现。阶段一不产生检查结果。
 */
public final class DefaultMedicalExaminationService implements MedicalExaminationService {

    @Override
    public Optional<MedicalObservationSnapshot> examine(ServerPlayer examiner, ServerPlayer patient, ResourceLocation examinationId) {
        return Optional.empty();
    }
}
