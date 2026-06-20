package com.redpred.livingsystem.domain.symptom;

import com.redpred.livingsystem.domain.body.BodyRegion;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 单个症状的状态（见开发文档 §9.2）。{@code intensity} 范围 0.0～1.0。不可变 {@code record}。
 * 症状只读取健康状态，不反向写入伤势或生理资源。
 */
public record SymptomState(
        ResourceLocation symptomId,
        SymptomTier tier,
        float intensity,
        Optional<BodyRegion> bodyRegion,
        Set<UUID> causeEffectIds
) {
}
