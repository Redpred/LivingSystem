package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 服务端→客户端：客户端需要的游戏性输出片段（见开发文档 §9.6、§26）。
 *
 * <p>移动/攻速/跳跃已通过属性自动同步，无需在此重复；这里只携带客户端本地需要的部分：挖掘倍率
 * （供 {@code BreakSpeed} 客户端预测一致）、镜头摇晃与心跳强度（供客户端表现）。</p>
 */
public record SyncGameplayPayload(float mining, float cameraSway, float heartbeat) implements CustomPacketPayload {

    public static final Type<SyncGameplayPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "sync_gameplay"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncGameplayPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SyncGameplayPayload::mining,
            ByteBufCodecs.FLOAT, SyncGameplayPayload::cameraSway,
            ByteBufCodecs.FLOAT, SyncGameplayPayload::heartbeat,
            SyncGameplayPayload::new);

    @Override
    public Type<SyncGameplayPayload> type() {
        return TYPE;
    }
}
