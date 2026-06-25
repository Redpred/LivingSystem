package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.effect.ExposureRoute;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

/**
 * 毒素定义（见开发文档 §5.8 中毒、§3.2.2）。
 *
 * <p>数据驱动毒素药代：吸收（未吸收→已吸收）、代谢（已吸收随时间清除）、毒性（已吸收量换算严重度）与
 * 致死阈值。当前症状（恶心、虚弱）由毒素总负荷通用派生；按毒素自定义靶器官与专属症状在后续细化。</p>
 *
 * @param id              定义 ID（约定与对应环境危害/物品 ID 一致以便接入）
 * @param descriptionZhCn 中文说明
 * @param enabled         是否启用
 * @param defaultRoute    默认给药途径
 * @param absorptionRate  每生理周期从未吸收转入已吸收的比例（0~1）
 * @param metabolismRate  每生理周期清除已吸收量的比例（0~1）
 * @param potency         已吸收量换算严重度的系数
 * @param lethalThreshold 致死的毒素负荷阈值（0 表示不致死）
 */
public record ToxinDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        ExposureRoute defaultRoute,
        float absorptionRate,
        float metabolismRate,
        float potency,
        float lethalThreshold
) implements RuleDefinition {

    public static final Codec<ToxinDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ToxinDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(ToxinDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(ToxinDefinition::enabled),
            EnumCodecs.of(ExposureRoute.class).optionalFieldOf("default_route", ExposureRoute.INGESTION)
                    .forGetter(ToxinDefinition::defaultRoute),
            Codec.FLOAT.optionalFieldOf("absorption_rate", 0.2F).forGetter(ToxinDefinition::absorptionRate),
            Codec.FLOAT.optionalFieldOf("metabolism_rate", 0.05F).forGetter(ToxinDefinition::metabolismRate),
            Codec.FLOAT.optionalFieldOf("potency", 1.0F).forGetter(ToxinDefinition::potency),
            Codec.FLOAT.optionalFieldOf("lethal_threshold", 0.0F).forGetter(ToxinDefinition::lethalThreshold)
    ).apply(instance, ToxinDefinition::new));
}
