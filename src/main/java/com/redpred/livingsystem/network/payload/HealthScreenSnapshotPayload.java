package com.redpred.livingsystem.network.payload;

import com.redpred.livingsystem.LivingSystemMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 服务端→客户端：健康主界面快照（见开发文档 §15.2、§26）。
 *
 * <p>携带七部位综合严重度、全身体征摘要与当前伤势条目，供客户端只读展示（信息分级由服务端在构建时决定，
 * 自查为主观/可观察级别）。客户端不据此推演权威数据。</p>
 *
 * @param regionSeverities 七个身体部位的综合严重度（0~1，顺序同 {@code BodyRegion.VALUES}）
 * @param vitals           全身体征摘要
 * @param injuries         当前伤势条目
 */
public record HealthScreenSnapshotPayload(
        List<Float> regionSeverities,
        VitalsSummary vitals,
        List<InjuryEntry> injuries
) implements CustomPacketPayload {

    /** 全身体征摘要（展示用）。 */
    public record VitalsSummary(
            float bloodFraction,
            float stamina,
            float hydration,
            float respiratory,
            float totalPain,
            float consciousness
    ) {
        public static final StreamCodec<ByteBuf, VitalsSummary> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, VitalsSummary::bloodFraction,
                ByteBufCodecs.FLOAT, VitalsSummary::stamina,
                ByteBufCodecs.FLOAT, VitalsSummary::hydration,
                ByteBufCodecs.FLOAT, VitalsSummary::respiratory,
                ByteBufCodecs.FLOAT, VitalsSummary::totalPain,
                ByteBufCodecs.FLOAT, VitalsSummary::consciousness,
                VitalsSummary::new);
    }

    /** 单条伤势展示条目。 */
    public record InjuryEntry(
            int regionIndex,
            String kindLabel,
            float severity,
            boolean bleeding,
            int fractureGrade,
            boolean treated
    ) {
        public static final StreamCodec<ByteBuf, InjuryEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, InjuryEntry::regionIndex,
                ByteBufCodecs.STRING_UTF8, InjuryEntry::kindLabel,
                ByteBufCodecs.FLOAT, InjuryEntry::severity,
                ByteBufCodecs.BOOL, InjuryEntry::bleeding,
                ByteBufCodecs.VAR_INT, InjuryEntry::fractureGrade,
                ByteBufCodecs.BOOL, InjuryEntry::treated,
                InjuryEntry::new);
    }

    public static final Type<HealthScreenSnapshotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "health_screen_snapshot"));

    public static final StreamCodec<ByteBuf, HealthScreenSnapshotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()), HealthScreenSnapshotPayload::regionSeverities,
            VitalsSummary.STREAM_CODEC, HealthScreenSnapshotPayload::vitals,
            InjuryEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), HealthScreenSnapshotPayload::injuries,
            HealthScreenSnapshotPayload::new);

    @Override
    public Type<HealthScreenSnapshotPayload> type() {
        return TYPE;
    }
}
