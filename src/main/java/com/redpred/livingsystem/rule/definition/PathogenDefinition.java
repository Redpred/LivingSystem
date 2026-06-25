package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.effect.PathogenType;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

/**
 * 病原体定义（见开发文档 §5.8 病原体、§3.2.2）。
 *
 * <p>数据驱动感染过程：潜伏期后进入症状期，载量按复制速率增长并受免疫清除对抗；载量换算严重度，
 * 达致死载量则致死，载量清零则痊愈。{@code woundInfectionChance} 用于开放污染伤口的继发感染判定。</p>
 *
 * @param id                  定义 ID
 * @param descriptionZhCn     中文说明
 * @param enabled             是否启用
 * @param type                病原体类型
 * @param incubationTicks     潜伏期（游戏刻），之后进入症状期
 * @param replicationRate     每周期载量增长比例（受免疫抑制）
 * @param virulence           载量换算严重度的系数
 * @param immuneClearRate     每周期免疫清除载量的比例（受免疫储备调制）
 * @param lethalLoad          致死载量阈值（0 表示不致死）
 * @param woundInfectionChance 开放污染伤口每周期继发本病原体的基础概率
 */
public record PathogenDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        PathogenType type,
        int incubationTicks,
        float replicationRate,
        float virulence,
        float immuneClearRate,
        float lethalLoad,
        float woundInfectionChance
) implements RuleDefinition {

    public static final Codec<PathogenDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(PathogenDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(PathogenDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(PathogenDefinition::enabled),
            EnumCodecs.of(PathogenType.class).optionalFieldOf("type", PathogenType.BACTERIA).forGetter(PathogenDefinition::type),
            Codec.INT.optionalFieldOf("incubation_ticks", 1200).forGetter(PathogenDefinition::incubationTicks),
            Codec.FLOAT.optionalFieldOf("replication_rate", 0.08F).forGetter(PathogenDefinition::replicationRate),
            Codec.FLOAT.optionalFieldOf("virulence", 1.0F).forGetter(PathogenDefinition::virulence),
            Codec.FLOAT.optionalFieldOf("immune_clear_rate", 0.05F).forGetter(PathogenDefinition::immuneClearRate),
            Codec.FLOAT.optionalFieldOf("lethal_load", 0.0F).forGetter(PathogenDefinition::lethalLoad),
            Codec.FLOAT.optionalFieldOf("wound_infection_chance", 0.0F).forGetter(PathogenDefinition::woundInfectionChance)
    ).apply(instance, PathogenDefinition::new));
}
