package com.redpred.livingsystem.bootstrap;

import com.redpred.livingsystem.network.handler.ClientPayloadReceiver;
import com.redpred.livingsystem.network.handler.ServerPayloadHandler;
import com.redpred.livingsystem.network.payload.CancelTreatmentRequest;
import com.redpred.livingsystem.network.payload.DeathReportPayload;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload;
import com.redpred.livingsystem.network.payload.HudSummaryPayload;
import com.redpred.livingsystem.network.payload.MedicalExaminationRequest;
import com.redpred.livingsystem.network.payload.MedicalObservationPayload;
import com.redpred.livingsystem.network.payload.OpenHealthScreenRequest;
import com.redpred.livingsystem.network.payload.RulesSummaryPayload;
import com.redpred.livingsystem.network.payload.StartTreatmentRequest;
import com.redpred.livingsystem.network.payload.SyncGameplayPayload;
import com.redpred.livingsystem.network.payload.TreatmentProgressPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 网络协议与 Payload 注册入口。
 *
 * <p>客户端发送的请求只包含标识与意图，服务端接收后必须重新校验（见开发文档 §26）。
 * 阶段一仅挂载注册监听器，具体 S2C/C2S Payload 在 {@code network} 层建立后在此登记。</p>
 */
public final class ModPayloads {

    /** 网络同步协议版本。 */
    public static final String PROTOCOL_VERSION = "1";

    private ModPayloads() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModPayloads::onRegister);
    }

    private static void onRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // 客户端 → 服务端请求（服务端重新校验入口）
        registrar.playToServer(OpenHealthScreenRequest.TYPE, OpenHealthScreenRequest.STREAM_CODEC,
                ServerPayloadHandler::handleOpenHealthScreen);
        registrar.playToServer(StartTreatmentRequest.TYPE, StartTreatmentRequest.STREAM_CODEC,
                ServerPayloadHandler::handleStartTreatment);
        registrar.playToServer(CancelTreatmentRequest.TYPE, CancelTreatmentRequest.STREAM_CODEC,
                ServerPayloadHandler::handleCancelTreatment);
        registrar.playToServer(MedicalExaminationRequest.TYPE, MedicalExaminationRequest.STREAM_CODEC,
                ServerPayloadHandler::handleMedicalExamination);

        // 服务端 → 客户端摘要（只读 handler 经 ClientHooks 保证 dist 安全）
        registrar.playToClient(HudSummaryPayload.TYPE, HudSummaryPayload.STREAM_CODEC,
                ClientPayloadReceiver::onHudSummary);
        registrar.playToClient(HealthScreenSnapshotPayload.TYPE, HealthScreenSnapshotPayload.STREAM_CODEC,
                ClientPayloadReceiver::onHealthScreenSnapshot);
        registrar.playToClient(TreatmentProgressPayload.TYPE, TreatmentProgressPayload.STREAM_CODEC,
                ClientPayloadReceiver::onTreatmentProgress);
        registrar.playToClient(MedicalObservationPayload.TYPE, MedicalObservationPayload.STREAM_CODEC,
                ClientPayloadReceiver::onMedicalObservation);
        registrar.playToClient(DeathReportPayload.TYPE, DeathReportPayload.STREAM_CODEC,
                ClientPayloadReceiver::onDeathReport);
        registrar.playToClient(RulesSummaryPayload.TYPE, RulesSummaryPayload.STREAM_CODEC,
                ClientPayloadReceiver::onRulesSummary);
        registrar.playToClient(SyncGameplayPayload.TYPE, SyncGameplayPayload.STREAM_CODEC,
                ClientPayloadReceiver::onGameplay);
    }
}
