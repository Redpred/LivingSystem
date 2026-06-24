package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.exposure.ExposureCategory;
import com.redpred.livingsystem.domain.exposure.ExposureTriggerMode;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 环境危害定义（见开发文档 §12 的 {@code EnvironmentalHazardProfile}）。
 *
 * <p>数据驱动：把方块/流体/群系映射为一类环境暴露。{@code trigger} 决定如何检测玩家是否处于危害中
 * （接触方块、浸没流体、吸入眼部介质、处于群系等）；{@code category} 决定累积后影响的生理系统
 * （高低温叠加环境温度、呼吸危害消耗呼吸储备、毒素/辐射累积剂量留阶段五）。</p>
 *
 * @param id               定义 ID
 * @param descriptionZhCn  中文说明
 * @param enabled          是否启用
 * @param category         暴露类别
 * @param trigger          触发方式
 * @param intensity        基础暴露强度（0~1）
 * @param temperatureDelta 热/冷类危害对环境温度的影响（摄氏度，可正可负）
 * @param blocks           匹配的方块 ID（接触/吸入类）
 * @param fluids           匹配的流体 ID（浸没/吸入类）
 * @param biomes           匹配的群系 ID（区域占据类）
 */
public record EnvironmentalHazardProfile(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        ExposureCategory category,
        ExposureTriggerMode trigger,
        float intensity,
        float temperatureDelta,
        List<ResourceLocation> blocks,
        List<ResourceLocation> fluids,
        List<ResourceLocation> biomes
) implements RuleDefinition {

    public static final Codec<EnvironmentalHazardProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(EnvironmentalHazardProfile::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(EnvironmentalHazardProfile::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(EnvironmentalHazardProfile::enabled),
            EnumCodecs.of(ExposureCategory.class).fieldOf("category").forGetter(EnvironmentalHazardProfile::category),
            EnumCodecs.of(ExposureTriggerMode.class).optionalFieldOf("trigger", ExposureTriggerMode.AREA_OCCUPANCY)
                    .forGetter(EnvironmentalHazardProfile::trigger),
            Codec.FLOAT.optionalFieldOf("intensity", 0.5F).forGetter(EnvironmentalHazardProfile::intensity),
            Codec.FLOAT.optionalFieldOf("temperature_delta", 0.0F).forGetter(EnvironmentalHazardProfile::temperatureDelta),
            ResourceLocation.CODEC.listOf().optionalFieldOf("blocks", List.of()).forGetter(EnvironmentalHazardProfile::blocks),
            ResourceLocation.CODEC.listOf().optionalFieldOf("fluids", List.of()).forGetter(EnvironmentalHazardProfile::fluids),
            ResourceLocation.CODEC.listOf().optionalFieldOf("biomes", List.of()).forGetter(EnvironmentalHazardProfile::biomes)
    ).apply(instance, EnvironmentalHazardProfile::new));
}
