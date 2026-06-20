package com.redpred.livingsystem.service.examination;

import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * {@link MedicalObservationRegistry} 默认实现。阶段一不保存任何检查结果。
 */
public final class DefaultMedicalObservationRegistry implements MedicalObservationRegistry {

    @Override
    public void store(ServerPlayer patient, MedicalObservationSnapshot observation) {
        // 阶段一占位
    }

    @Override
    public List<MedicalObservationSnapshot> get(ServerPlayer patient) {
        return List.of();
    }
}
