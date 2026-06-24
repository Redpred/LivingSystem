package com.redpred.livingsystem.service.examination;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import com.redpred.livingsystem.service.LivingServices;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * {@link MedicalObservationRegistry} 默认实现（阶段三 3.5）。把检查结果保存进玩家聚合根的观察列表，
 * 仅保留有限数量的最近结果（见开发文档 §20 持久化权威状态）。
 */
public final class DefaultMedicalObservationRegistry implements MedicalObservationRegistry {

    /** 每名玩家保留的最大检查结果数量。 */
    private static final int MAX_OBSERVATIONS = 8;

    @Override
    public void store(ServerPlayer patient, MedicalObservationSnapshot observation) {
        PlayerHealthData data = LivingServices.REPOSITORY.get(patient);
        List<MedicalObservationSnapshot> observations = data.observations();
        observations.add(observation);
        while (observations.size() > MAX_OBSERVATIONS) {
            observations.remove(0);
        }
    }

    @Override
    public List<MedicalObservationSnapshot> get(ServerPlayer patient) {
        return LivingServices.REPOSITORY.get(patient).observations();
    }
}
