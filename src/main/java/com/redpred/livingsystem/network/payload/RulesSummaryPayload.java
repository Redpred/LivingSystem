package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 服务端→客户端：服务端规则摘要（见开发文档 §15.4.3、§26）。客户端只读显示当前规则版本与总开关。
 */
public record RulesSummaryPayload(long rulesVersion, boolean masterEnabled) implements CustomPacketPayload {

    public static final Type<RulesSummaryPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "rules_summary"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RulesSummaryPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, RulesSummaryPayload::rulesVersion,
            ByteBufCodecs.BOOL, RulesSummaryPayload::masterEnabled,
            RulesSummaryPayload::new);

    @Override
    public Type<RulesSummaryPayload> type() {
        return TYPE;
    }
}
