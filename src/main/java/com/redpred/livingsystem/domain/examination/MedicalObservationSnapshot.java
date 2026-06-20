package com.redpred.livingsystem.domain.examination;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

/**
 * 一次医疗设备检查得到的结果（见开发文档 §15.1.3）。
 *
 * <p>是某一时刻的测量快照，带有效时长与精度；超过有效时长后过期，不是实时读取内部数据库。
 * 不可变 {@code record}。</p>
 */
public record MedicalObservationSnapshot(
        UUID id,
        UUID patientId,
        ResourceLocation examinationId,
        MedicalInformationLevel informationLevel,
        Map<ResourceLocation, MedicalObservationValue> values,
        long measuredGameTime,
        long validityDuration,
        float accuracy
) {
}
