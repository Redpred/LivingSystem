package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 伤害来源（DamageType）到伤害画像的映射定义（见开发文档 §3.2.2、§5）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（致伤机制集合、严重度映射、结构损伤权重、伤口生成概率等）
 * 在后续阶段按文档填充。</p>
 */
public record DamageProfile(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<DamageProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(DamageProfile::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(DamageProfile::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(DamageProfile::enabled)
    ).apply(instance, DamageProfile::new));
}
