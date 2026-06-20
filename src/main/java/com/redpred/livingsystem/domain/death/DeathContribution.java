package com.redpred.livingsystem.domain.death;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

/**
 * 死亡因果链中的一个促成因素（见开发文档 §14.8）。不可变 {@code record}。
 *
 * <p>注：开发文档列出 {@code majorContributions/secondaryContributions} 但未给字段，此处按因果回溯
 * 需要合理设计：原因 ID、相关健康影响实例、权重与中文描述。</p>
 */
public record DeathContribution(
        ResourceLocation causeId,
        Optional<UUID> effectId,
        float weight,
        String descriptionZhCn
) {
}
