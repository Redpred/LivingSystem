package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 客户端→服务端：医疗检查请求（见开发文档 §15.1.5、§26）。服务端重新验证距离、设备与状态。
 */
public record MedicalExaminationRequest(UUID patientId, ResourceLocation examinationId) implements CustomPacketPayload {

    public static final Type<MedicalExaminationRequest> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "medical_examination_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MedicalExaminationRequest> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, MedicalExaminationRequest::patientId,
            ResourceLocation.STREAM_CODEC, MedicalExaminationRequest::examinationId,
            MedicalExaminationRequest::new);

    @Override
    public Type<MedicalExaminationRequest> type() {
        return TYPE;
    }
}
