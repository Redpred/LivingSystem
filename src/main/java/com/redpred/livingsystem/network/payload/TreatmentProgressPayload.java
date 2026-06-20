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
 * 服务端→客户端：治疗进度（见开发文档 §13.3.3）。进度只读显示，客户端不得据此自行完成治疗。
 */
public record TreatmentProgressPayload(UUID sessionId, float progress, int remainingTicks, boolean interrupted)
        implements CustomPacketPayload {

    public static final Type<TreatmentProgressPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "treatment_progress"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TreatmentProgressPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TreatmentProgressPayload::sessionId,
            ByteBufCodecs.FLOAT, TreatmentProgressPayload::progress,
            ByteBufCodecs.VAR_INT, TreatmentProgressPayload::remainingTicks,
            ByteBufCodecs.BOOL, TreatmentProgressPayload::interrupted,
            TreatmentProgressPayload::new);

    @Override
    public Type<TreatmentProgressPayload> type() {
        return TYPE;
    }
}
