package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 客户端→服务端：取消治疗请求（见开发文档 §26）。
 */
public record CancelTreatmentRequest(UUID sessionId) implements CustomPacketPayload {

    public static final Type<CancelTreatmentRequest> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "cancel_treatment_request"));

    public static final StreamCodec<ByteBuf, CancelTreatmentRequest> STREAM_CODEC =
            UUIDUtil.STREAM_CODEC.map(CancelTreatmentRequest::new, CancelTreatmentRequest::sessionId);

    @Override
    public Type<CancelTreatmentRequest> type() {
        return TYPE;
    }
}
