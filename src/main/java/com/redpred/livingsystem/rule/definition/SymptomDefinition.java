package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 症状定义（见开发文档 §3.8）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（SymptomTrigger 触发器、IntensityCurve 强度曲线、
 * 游戏输出列表、StackingPolicy 叠加策略、进入/退出迟滞阈值等）在后续阶段按文档填充。</p>
 */
public record SymptomDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<SymptomDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SymptomDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(SymptomDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SymptomDefinition::enabled)
    ).apply(instance, SymptomDefinition::new));
}
