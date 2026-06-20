package com.redpred.livingsystem.client;

import com.redpred.livingsystem.client.screen.HealthScreen;
import com.redpred.livingsystem.client.state.ClientHealthState;
import com.redpred.livingsystem.network.payload.DeathReportPayload;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload;
import com.redpred.livingsystem.network.payload.HudSummaryPayload;
import com.redpred.livingsystem.network.payload.MedicalObservationPayload;
import com.redpred.livingsystem.network.payload.RulesSummaryPayload;
import com.redpred.livingsystem.network.payload.TreatmentProgressPayload;
import net.minecraft.client.Minecraft;

/**
 * 客户端专用钩子集合。<b>这是唯一直接引用 {@code net.minecraft.client.*} 的网络/界面入口</b>，
 * 仅经由惰性 lambda 从 dist 安全的接收器调用，绝不在专用服务器加载（见 §27 与既有 invalid-dist 教训）。
 */
public final class ClientHooks {

    private ClientHooks() {
    }

    public static void applyHudSummary(HudSummaryPayload payload) {
        ClientHealthState.setHud(payload);
    }

    public static void applyHealthScreenSnapshot(HealthScreenSnapshotPayload payload) {
        ClientHealthState.setHealthScreen(payload);
    }

    public static void applyTreatmentProgress(TreatmentProgressPayload payload) {
        ClientHealthState.setTreatmentProgress(payload);
    }

    public static void applyMedicalObservation(MedicalObservationPayload payload) {
        ClientHealthState.setLastObservation(payload);
    }

    public static void applyDeathReport(DeathReportPayload payload) {
        ClientHealthState.setLastDeathReport(payload);
    }

    public static void applyRulesSummary(RulesSummaryPayload payload) {
        ClientHealthState.setRulesSummary(payload);
    }

    /** 打开健康主界面。 */
    public static void openHealthScreen() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null) {
            minecraft.setScreen(new HealthScreen());
        }
    }
}
