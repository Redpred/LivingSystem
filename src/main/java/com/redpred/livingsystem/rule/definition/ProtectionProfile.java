package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 防护装备定义（见开发文档 §11.3 的 {@code ProtectionProfile}）。
 *
 * <p>数据驱动：把物品映射为对创伤与环境暴露的防护。创伤防护降低穿透严重度、结构损伤与伤口/骨折生成；
 * 环境防护提供隔热、呼吸过滤/密封与辐射屏蔽。覆盖部位为空表示全身生效（如防护服、面具）。所有抗性为
 * 0~1 的减免比例。原版护甲减伤仍只计算一次，本定义只处理 LivingSystem 专用层（见 §11.8）。</p>
 *
 * @param id                   定义 ID
 * @param descriptionZhCn      中文说明
 * @param enabled              是否启用
 * @param items                匹配的物品 ID
 * @param coveredRegions       覆盖的身体部位（为空表示全身）
 * @param penetrationResist    穿透/严重度抗性（0~1）
 * @param structureProtection  结构损伤减免（0~1）
 * @param woundChanceReduction 伤口/骨折生成减免（0~1）
 * @param thermalInsulation    隔热（0~1，减少环境温度对体温的偏离）
 * @param respiratoryFiltration 呼吸过滤/密封（0~1，减少呼吸类暴露）
 * @param radiationShielding   辐射屏蔽（0~1，阶段五接入）
 */
public record ProtectionProfile(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        List<ResourceLocation> items,
        List<BodyRegion> coveredRegions,
        float penetrationResist,
        float structureProtection,
        float woundChanceReduction,
        float thermalInsulation,
        float respiratoryFiltration,
        float radiationShielding
) implements RuleDefinition {

    public static final Codec<ProtectionProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ProtectionProfile::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(ProtectionProfile::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(ProtectionProfile::enabled),
            ResourceLocation.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(ProtectionProfile::items),
            EnumCodecs.of(BodyRegion.class).listOf().optionalFieldOf("covered_regions", List.of())
                    .forGetter(ProtectionProfile::coveredRegions),
            Codec.FLOAT.optionalFieldOf("penetration_resist", 0.0F).forGetter(ProtectionProfile::penetrationResist),
            Codec.FLOAT.optionalFieldOf("structure_protection", 0.0F).forGetter(ProtectionProfile::structureProtection),
            Codec.FLOAT.optionalFieldOf("wound_chance_reduction", 0.0F).forGetter(ProtectionProfile::woundChanceReduction),
            Codec.FLOAT.optionalFieldOf("thermal_insulation", 0.0F).forGetter(ProtectionProfile::thermalInsulation),
            Codec.FLOAT.optionalFieldOf("respiratory_filtration", 0.0F).forGetter(ProtectionProfile::respiratoryFiltration),
            Codec.FLOAT.optionalFieldOf("radiation_shielding", 0.0F).forGetter(ProtectionProfile::radiationShielding)
    ).apply(instance, ProtectionProfile::new));
}
