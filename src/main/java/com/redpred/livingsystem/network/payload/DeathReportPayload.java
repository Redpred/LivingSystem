package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 服务端→客户端：死亡报告（见开发文档 §14.8）。阶段一仅携带报告 ID 骨架，完整报告内容后续阶段扩展。
 */
public record DeathReportPayload(UUID reportId) implements CustomPacketPayload {

    public static final Type<DeathReportPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "death_report"));

    public static final StreamCodec<ByteBuf, DeathReportPayload> STREAM_CODEC =
            UUIDUtil.STREAM_CODEC.map(DeathReportPayload::new, DeathReportPayload::reportId);

    @Override
    public Type<DeathReportPayload> type() {
        return TYPE;
    }
}
