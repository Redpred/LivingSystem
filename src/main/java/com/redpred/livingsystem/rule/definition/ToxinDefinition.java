package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 毒素定义（见开发文档 §5.8 中毒、§3.2.2）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（吸收/代谢、靶结构、症状、解毒剂、给药途径等）在后续
 * 阶段按文档填充。</p>
 */
public record ToxinDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<ToxinDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ToxinDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(ToxinDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(ToxinDefinition::enabled)
    ).apply(instance, ToxinDefinition::new));
}
