package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * 防护装备定义（见开发文档 §11.3 的 {@code ProtectionProfile}）。
 *
 * <p>阶段一为骨架：仅含通用字段；完整字段（物品匹配、覆盖部位、各致伤机制抗性、穿透抗性、密封性、
 * 呼吸/生物/化学防护、辐射屏蔽、高低温防护、耗材、完整度曲线等）在后续阶段按文档填充。</p>
 */
public record ProtectionProfile(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled
) implements RuleDefinition {

    public static final Codec<ProtectionProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ProtectionProfile::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(ProtectionProfile::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(ProtectionProfile::enabled)
    ).apply(instance, ProtectionProfile::new));
}
