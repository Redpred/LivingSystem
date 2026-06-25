package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.effect.RadiationType;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

/**
 * 辐射源定义（见开发文档 §5.8 辐射、§3.2.2）。约定定义 ID 为辐射类型名小写（如 {@code livingsystem:gamma}），
 * 供辐射引擎按类型查询。
 *
 * <p>辐射主要累积剂量，超过症状阈值后产生生物效应（恶心/虚弱），达致死剂量则致死；剂量随时间按
 * {@code decayRate} 缓慢排出/衰减。不每刻调用原版扣血（见 §5.8）。</p>
 *
 * @param id              定义 ID
 * @param descriptionZhCn 中文说明
 * @param enabled         是否启用
 * @param type            辐射类型
 * @param symptomThreshold 产生症状的累计剂量阈值
 * @param lethalDose      致死的累计剂量阈值（0 表示不致死）
 * @param decayRate       每周期剂量衰减/排出比例
 */
public record RadiationDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        RadiationType type,
        float symptomThreshold,
        float lethalDose,
        float decayRate
) implements RuleDefinition {

    public static final Codec<RadiationDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(RadiationDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(RadiationDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(RadiationDefinition::enabled),
            EnumCodecs.of(RadiationType.class).optionalFieldOf("type", RadiationType.GAMMA).forGetter(RadiationDefinition::type),
            Codec.FLOAT.optionalFieldOf("symptom_threshold", 0.3F).forGetter(RadiationDefinition::symptomThreshold),
            Codec.FLOAT.optionalFieldOf("lethal_dose", 0.0F).forGetter(RadiationDefinition::lethalDose),
            Codec.FLOAT.optionalFieldOf("decay_rate", 0.01F).forGetter(RadiationDefinition::decayRate)
    ).apply(instance, RadiationDefinition::new));
}
