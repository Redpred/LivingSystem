package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 辐射源定义（见开发文档 §5.8 辐射、§3.2.2）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（辐射类型、剂量率、阶段阈值、生物效应等）在后续阶段
 * 按文档填充。</p>
 */
public record RadiationDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<RadiationDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(RadiationDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(RadiationDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(RadiationDefinition::enabled)
    ).apply(instance, RadiationDefinition::new));
}
