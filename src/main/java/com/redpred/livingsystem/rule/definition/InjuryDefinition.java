package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 伤势定义（见开发文档 §3.3）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（生成概率、最低有效伤害、最大生成数量、基础严重度、
 * 穿透深度、结构损伤权重、各专用参数等）在后续阶段按文档填充。</p>
 */
public record InjuryDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<InjuryDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(InjuryDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(InjuryDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(InjuryDefinition::enabled)
    ).apply(instance, InjuryDefinition::new));
}
