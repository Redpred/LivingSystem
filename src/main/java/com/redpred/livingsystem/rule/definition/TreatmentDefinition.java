package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 治疗行为定义（见开发文档 §13.16 的 {@code TreatmentActionDefinition}）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（目标类型、治疗目的、时长、提交策略、基础速率、兼容目标、
 * 前置条件、操作、副作用、是否允许自我治疗、是否需要静止等）在后续阶段按文档填充。</p>
 */
public record TreatmentDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<TreatmentDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(TreatmentDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(TreatmentDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(TreatmentDefinition::enabled)
    ).apply(instance, TreatmentDefinition::new));
}
