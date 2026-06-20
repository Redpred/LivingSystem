package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.effect.HarmMechanism;
import com.redpred.livingsystem.domain.effect.TraumaKind;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

/**
 * 伤害来源（DamageType）到伤害画像的映射定义（见开发文档 §3.2.2、§5、§10）。
 *
 * <p>把一种 DamageType 转换为统一致伤机制与一个机械创伤类型，并给出基础严重度、结构损伤权重、
 * 单位严重度失血量与骨折/脑震荡概率。阶段二 2.1 起启用机械相关字段；非机械字段后续阶段扩展。</p>
 *
 * @param mechanisms          该来源产生的致伤机制集合
 * @param traumaKind          生成的机械创伤类型
 * @param baseSeverity        基础严重度（0~1，命中与配置再做缩放）
 * @param structureWeights    对各解剖结构的损伤权重（0~1）
 * @param bloodLossPerSeverity 每点严重度造成的急性失血量（毫升）
 * @param fractureChance      触发骨折的基础概率
 * @param concussionChance    头部触发脑震荡的基础概率
 */
public record DamageProfile(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        List<HarmMechanism> mechanisms,
        TraumaKind traumaKind,
        float baseSeverity,
        Map<AnatomicalStructure, Float> structureWeights,
        float bloodLossPerSeverity,
        float fractureChance,
        float concussionChance
) implements RuleDefinition {

    public static final Codec<DamageProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(DamageProfile::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(DamageProfile::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(DamageProfile::enabled),
            EnumCodecs.of(HarmMechanism.class).listOf().optionalFieldOf("mechanisms", List.of())
                    .forGetter(DamageProfile::mechanisms),
            EnumCodecs.of(TraumaKind.class).optionalFieldOf("trauma_kind", TraumaKind.CONTUSION)
                    .forGetter(DamageProfile::traumaKind),
            Codec.FLOAT.optionalFieldOf("base_severity", 0.3F).forGetter(DamageProfile::baseSeverity),
            Codec.unboundedMap(EnumCodecs.of(AnatomicalStructure.class), Codec.FLOAT)
                    .optionalFieldOf("structure_weights", Map.of()).forGetter(DamageProfile::structureWeights),
            Codec.FLOAT.optionalFieldOf("blood_loss_per_severity", 200.0F).forGetter(DamageProfile::bloodLossPerSeverity),
            Codec.FLOAT.optionalFieldOf("fracture_chance", 0.0F).forGetter(DamageProfile::fractureChance),
            Codec.FLOAT.optionalFieldOf("concussion_chance", 0.0F).forGetter(DamageProfile::concussionChance)
    ).apply(instance, DamageProfile::new));
}
