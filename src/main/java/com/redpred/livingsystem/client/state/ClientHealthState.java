package com.redpred.livingsystem.client.state;

import com.redpred.livingsystem.network.payload.DeathReportPayload;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload;
import com.redpred.livingsystem.network.payload.HudSummaryPayload;
import com.redpred.livingsystem.network.payload.MedicalObservationPayload;
import com.redpred.livingsystem.network.payload.RulesSummaryPayload;
import com.redpred.livingsystem.network.payload.SyncGameplayPayload;
import com.redpred.livingsystem.network.payload.TreatmentProgressPayload;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端只读健康状态（见开发文档 §27）。仅保存服务端同步来的最近快照供 HUD/界面/表现显示，
 * 客户端不得持有或推演完整权威健康数据库。
 *
 * <p>本类不引用 {@code net.minecraft.client.*}，仅持有公共网络 Payload，便于被 dist 安全的接收器写入
 * 与公共事件处理器读取。</p>
 */
public final class ClientHealthState {

    @Nullable private static volatile HudSummaryPayload hud;
    @Nullable private static volatile HealthScreenSnapshotPayload healthScreen;
    @Nullable private static volatile TreatmentProgressPayload treatmentProgress;
    @Nullable private static volatile MedicalObservationPayload lastObservation;
    @Nullable private static volatile DeathReportPayload lastDeathReport;
    @Nullable private static volatile RulesSummaryPayload rulesSummary;
    @Nullable private static volatile SyncGameplayPayload gameplay;

    private ClientHealthState() {
    }

    public static void setHud(HudSummaryPayload value) { hud = value; }
    @Nullable public static HudSummaryPayload getHud() { return hud; }

    public static void setHealthScreen(HealthScreenSnapshotPayload value) { healthScreen = value; }
    @Nullable public static HealthScreenSnapshotPayload getHealthScreen() { return healthScreen; }

    public static void setTreatmentProgress(TreatmentProgressPayload value) { treatmentProgress = value; }
    @Nullable public static TreatmentProgressPayload getTreatmentProgress() { return treatmentProgress; }

    public static void setLastObservation(MedicalObservationPayload value) { lastObservation = value; }
    @Nullable public static MedicalObservationPayload getLastObservation() { return lastObservation; }

    public static void setLastDeathReport(DeathReportPayload value) { lastDeathReport = value; }
    @Nullable public static DeathReportPayload getLastDeathReport() { return lastDeathReport; }

    public static void setRulesSummary(RulesSummaryPayload value) { rulesSummary = value; }
    @Nullable public static RulesSummaryPayload getRulesSummary() { return rulesSummary; }

    public static void setGameplay(SyncGameplayPayload value) { gameplay = value; }
    @Nullable public static SyncGameplayPayload getGameplay() { return gameplay; }

    /** 当前挖掘倍率（无数据时为 1）。供 {@code BreakSpeed} 客户端预测使用。 */
    public static float gameplayMining() {
        SyncGameplayPayload g = gameplay;
        return g != null ? g.mining() : 1.0F;
    }

    /** 当前镜头摇晃强度（无数据时为 0）。 */
    public static float gameplaySway() {
        SyncGameplayPayload g = gameplay;
        return g != null ? g.cameraSway() : 0.0F;
    }

    /** 当前心跳强度（无数据时为 0）。 */
    public static float gameplayHeartbeat() {
        SyncGameplayPayload g = gameplay;
        return g != null ? g.heartbeat() : 0.0F;
    }
}
