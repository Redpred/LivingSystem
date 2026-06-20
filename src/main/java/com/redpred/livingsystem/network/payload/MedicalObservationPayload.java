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
 * 服务端→客户端：一次医疗检查结果（见开发文档 §15.1.3）。阶段一为最小字段骨架（结果 ID 与精度）。
 */
public record MedicalObservationPayload(UUID observationId, float accuracy) implements CustomPacketPayload {

    public static final Type<MedicalObservationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "medical_observation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MedicalObservationPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, MedicalObservationPayload::observationId,
            ByteBufCodecs.FLOAT, MedicalObservationPayload::accuracy,
            MedicalObservationPayload::new);

    @Override
    public Type<MedicalObservationPayload> type() {
        return TYPE;
    }
}
