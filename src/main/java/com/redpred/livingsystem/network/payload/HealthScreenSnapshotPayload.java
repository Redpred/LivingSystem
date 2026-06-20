package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 服务端→客户端：健康主界面快照（见开发文档 §26）。阶段一以七部位综合严重度列表表示，后续阶段扩展。
 */
public record HealthScreenSnapshotPayload(List<Float> regionSeverities) implements CustomPacketPayload {

    public static final Type<HealthScreenSnapshotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "health_screen_snapshot"));

    public static final StreamCodec<ByteBuf, HealthScreenSnapshotPayload> STREAM_CODEC =
            ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list())
                    .map(HealthScreenSnapshotPayload::new, HealthScreenSnapshotPayload::regionSeverities);

    @Override
    public Type<HealthScreenSnapshotPayload> type() {
        return TYPE;
    }
}
