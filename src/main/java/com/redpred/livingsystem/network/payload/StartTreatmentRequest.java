package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 客户端→服务端：开始治疗请求（见开发文档 §13.22、§26）。
 *
 * <p>只包含标识与意图，服务端必须重新验证全部条件。{@code targetEffectId} 为全零 UUID 表示无具体目标
 * （系统性治疗）。</p>
 */
public record StartTreatmentRequest(UUID patientId, UUID targetEffectId, ResourceLocation treatmentActionId, int itemSlot)
        implements CustomPacketPayload {

    public static final Type<StartTreatmentRequest> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "start_treatment_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StartTreatmentRequest> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, StartTreatmentRequest::patientId,
            UUIDUtil.STREAM_CODEC, StartTreatmentRequest::targetEffectId,
            ResourceLocation.STREAM_CODEC, StartTreatmentRequest::treatmentActionId,
            ByteBufCodecs.VAR_INT, StartTreatmentRequest::itemSlot,
            StartTreatmentRequest::new);

    @Override
    public Type<StartTreatmentRequest> type() {
        return TYPE;
    }
}
