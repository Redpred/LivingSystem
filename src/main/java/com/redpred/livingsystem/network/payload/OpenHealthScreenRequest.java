package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 客户端→服务端：请求打开健康界面（见开发文档 §26）。无负载；服务端据此回送界面快照。
 */
public record OpenHealthScreenRequest() implements CustomPacketPayload {

    public static final Type<OpenHealthScreenRequest> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "open_health_screen_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenHealthScreenRequest> STREAM_CODEC =
            StreamCodec.unit(new OpenHealthScreenRequest());

    @Override
    public Type<OpenHealthScreenRequest> type() {
        return TYPE;
    }
}
