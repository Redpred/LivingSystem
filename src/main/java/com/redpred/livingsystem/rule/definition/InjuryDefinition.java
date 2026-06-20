package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.effect.TraumaKind;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * 伤势定义（见开发文档 §3.3、§5.5）。描述某机械创伤类型的固有参数：严重度缩放、穿透深度、
 * 结构损伤权重。阶段二 2.1 起启用机械相关字段；出血/疼痛/愈合等细化字段后续阶段扩展。
 */
public record InjuryDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        TraumaKind traumaKind,
        float severityScale,
        float depth,
        Map<AnatomicalStructure, Float> structureWeights
) implements RuleDefinition {

    public static final Codec<InjuryDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(InjuryDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(InjuryDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(InjuryDefinition::enabled),
            EnumCodecs.of(TraumaKind.class).optionalFieldOf("trauma_kind", TraumaKind.CONTUSION)
                    .forGetter(InjuryDefinition::traumaKind),
            Codec.FLOAT.optionalFieldOf("severity_scale", 1.0F).forGetter(InjuryDefinition::severityScale),
            Codec.FLOAT.optionalFieldOf("depth", 0.3F).forGetter(InjuryDefinition::depth),
            Codec.unboundedMap(EnumCodecs.of(AnatomicalStructure.class), Codec.FLOAT)
                    .optionalFieldOf("structure_weights", Map.of()).forGetter(InjuryDefinition::structureWeights)
    ).apply(instance, InjuryDefinition::new));
}
