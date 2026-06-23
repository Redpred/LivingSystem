package com.redpred.livingsystem.network.handler;

import com.redpred.livingsystem.client.ClientHooks;
import com.redpred.livingsystem.network.payload.DeathReportPayload;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload;
import com.redpred.livingsystem.network.payload.HudSummaryPayload;
import com.redpred.livingsystem.network.payload.MedicalObservationPayload;
import com.redpred.livingsystem.network.payload.RulesSummaryPayload;
import com.redpred.livingsystem.network.payload.SyncGameplayPayload;
import com.redpred.livingsystem.network.payload.TreatmentProgressPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端→客户端 Payload 的接收入口。
 *
 * <p><b>dist 安全：</b>本类被 {@code ModPayloads}（双端加载）以方法引用注册，因此<b>绝不</b>引用
 * {@code net.minecraft.client.*}。真正的客户端处理在惰性 lambda 内委托给 {@link ClientHooks}
 * （client-only，仅在客户端执行 handler 时才加载），从而不会在专用服务器链接客户端类。</p>
 */
public final class ClientPayloadReceiver {

    private ClientPayloadReceiver() {
    }

    public static void onHudSummary(HudSummaryPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHooks.applyHudSummary(payload));
    }

    public static void onHealthScreenSnapshot(HealthScreenSnapshotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHooks.applyHealthScreenSnapshot(payload));
    }

    public static void onTreatmentProgress(TreatmentProgressPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHooks.applyTreatmentProgress(payload));
    }

    public static void onMedicalObservation(MedicalObservationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHooks.applyMedicalObservation(payload));
    }

    public static void onDeathReport(DeathReportPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHooks.applyDeathReport(payload));
    }

    public static void onRulesSummary(RulesSummaryPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHooks.applyRulesSummary(payload));
    }

    public static void onGameplay(SyncGameplayPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHooks.applyGameplay(payload));
    }
}
