package com.redpred.livingsystem.domain.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

/**
 * 伤害或暴露来源的不可变快照。
 *
 * <p>用于在健康影响实例中记录“伤从何来”，便于死亡报告回溯与调试。遵守持久化约束（见开发文档
 * §21）：不保存 {@code Entity}、{@code Level}、{@code DamageSource} 或完整 {@code ItemStack}，
 * 只保存其标识（{@link ResourceLocation} 或 {@link UUID}）。</p>
 *
 * @param damageTypeId      原版/自定义伤害类型 ID
 * @param directEntityType  直接来源实体类型（投射物、攻击实体等）
 * @param causingEntityType 最终攻击者实体类型
 * @param sourceItemId      相关武器或物品 ID
 * @param directEntityId    直接来源实体 UUID
 * @param causingEntityId   最终攻击者实体 UUID
 */
public record CauseSnapshot(
        Optional<ResourceLocation> damageTypeId,
        Optional<ResourceLocation> directEntityType,
        Optional<ResourceLocation> causingEntityType,
        Optional<ResourceLocation> sourceItemId,
        Optional<UUID> directEntityId,
        Optional<UUID> causingEntityId
) {
    /** 持久化 Codec。 */
    public static final Codec<CauseSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("damage_type_id").forGetter(CauseSnapshot::damageTypeId),
            ResourceLocation.CODEC.optionalFieldOf("direct_entity_type").forGetter(CauseSnapshot::directEntityType),
            ResourceLocation.CODEC.optionalFieldOf("causing_entity_type").forGetter(CauseSnapshot::causingEntityType),
            ResourceLocation.CODEC.optionalFieldOf("source_item_id").forGetter(CauseSnapshot::sourceItemId),
            UUIDUtil.CODEC.optionalFieldOf("direct_entity_id").forGetter(CauseSnapshot::directEntityId),
            UUIDUtil.CODEC.optionalFieldOf("causing_entity_id").forGetter(CauseSnapshot::causingEntityId)
    ).apply(instance, CauseSnapshot::new));

    /** 无法识别来源时使用的空快照。 */
    public static final CauseSnapshot UNKNOWN = new CauseSnapshot(
            Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty());
}
