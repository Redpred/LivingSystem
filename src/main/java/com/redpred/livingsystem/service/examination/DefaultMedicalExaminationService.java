package com.redpred.livingsystem.service.examination;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.examination.MedicalInformationLevel;
import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import com.redpred.livingsystem.domain.examination.MedicalObservationValue;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.physiology.DefaultPhysiologyEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link MedicalExaminationService} 默认实现（阶段三 3.5）。
 *
 * <p>对患者当前权威生理状态做一次测量，生成 {@code MEASURED} 级别的观察快照（精确体征数值，带时效与精度）。
 * 检查器械与更高 {@code DIAGNOSED} 级别在阶段六医疗设备接入；此处提供基础体征测量。</p>
 */
public final class DefaultMedicalExaminationService implements MedicalExaminationService {

    /** 观察结果有效时长（游戏刻，约 30 秒）。 */
    private static final long VALIDITY_TICKS = 600L;

    @Override
    public Optional<MedicalObservationSnapshot> examine(ServerPlayer examiner, ServerPlayer patient, ResourceLocation examinationId) {
        PlayerHealthData data = LivingServices.REPOSITORY.get(patient);
        PhysiologyState p = data.physiology();

        Map<ResourceLocation, MedicalObservationValue> values = new LinkedHashMap<>();
        float bloodFraction = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;
        put(values, "blood_volume", p.getCurrentBloodVolume(),
                String.format("血容量 %.0f mL（%d%%）", p.getCurrentBloodVolume(), Math.round(bloodFraction * 100)));
        put(values, "core_temperature", p.getCoreTemperature(),
                String.format("核心体温 %.1f℃", p.getCoreTemperature()));
        put(values, "respiratory_reserve", p.getRespiratoryReserve(),
                String.format("呼吸储备 %d%%", Math.round(p.getRespiratoryReserve() * 100)));
        put(values, "oxygen_debt", p.getOxygenDebt(),
                String.format("氧债 %d%%", Math.round(p.getOxygenDebt() * 100)));
        put(values, "consciousness", p.getConsciousness(),
                String.format("意识 %d%%", Math.round(p.getConsciousness() * 100)));
        float pain = DefaultPhysiologyEngine.aggregateTotalPain(data, p);
        put(values, "total_pain", pain, String.format("总疼痛 %d%%", Math.round(pain * 100)));

        MedicalObservationSnapshot snapshot = new MedicalObservationSnapshot(
                UUID.randomUUID(), patient.getUUID(), examinationId, MedicalInformationLevel.MEASURED,
                Map.copyOf(values), patient.level().getGameTime(), VALIDITY_TICKS, 1.0F);
        return Optional.of(snapshot);
    }

    private static void put(Map<ResourceLocation, MedicalObservationValue> map, String key, double value, String display) {
        map.put(ResourceLocation.fromNamespaceAndPath("livingsystem", key), new MedicalObservationValue(value, display));
    }
}
