package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 病原体定义（见开发文档 §5.8 病原体、§3.2.2）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（类型、传播途径、阶段时长、复制速率、免疫控制、症状、
 * 治疗响应等）在后续阶段按文档填充。</p>
 */
public record PathogenDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<PathogenDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(PathogenDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(PathogenDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(PathogenDefinition::enabled)
    ).apply(instance, PathogenDefinition::new));
}
