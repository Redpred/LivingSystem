package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 服务端→客户端：HUD 摘要（见开发文档 §26）。显示值为映射到 0～20 区间的展示值，不反向修改权威状态。
 * 阶段一为最小字段骨架。
 */
public record HudSummaryPayload(float blood, float stamina, float hydration, float respiratory)
        implements CustomPacketPayload {

    public static final Type<HudSummaryPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "hud_summary"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HudSummaryPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, HudSummaryPayload::blood,
            ByteBufCodecs.FLOAT, HudSummaryPayload::stamina,
            ByteBufCodecs.FLOAT, HudSummaryPayload::hydration,
            ByteBufCodecs.FLOAT, HudSummaryPayload::respiratory,
            HudSummaryPayload::new);

    @Override
    public Type<HudSummaryPayload> type() {
        return TYPE;
    }
}
