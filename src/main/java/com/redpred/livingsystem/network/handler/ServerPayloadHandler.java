package com.redpred.livingsystem.network.handler;

import com.redpred.livingsystem.network.payload.CancelTreatmentRequest;
import com.redpred.livingsystem.network.payload.MedicalExaminationRequest;
import com.redpred.livingsystem.network.payload.OpenHealthScreenRequest;
import com.redpred.livingsystem.network.payload.StartTreatmentRequest;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端请求的服务端处理入口（见开发文档 §26）。
 *
 * <p>所有处理在逻辑主线程执行（{@link IPayloadContext#enqueueWork}），且服务端必须重新验证玩家身份、
 * 权限、距离、目标存在、物品槽位、会话状态与速率限制，不信任客户端提交的数值与目标状态。
 * 阶段一为校验骨架（占位），具体校验与服务调用在后续阶段接入。</p>
 */
public final class ServerPayloadHandler {

    private ServerPayloadHandler() {
    }

    public static void handleOpenHealthScreen(OpenHealthScreenRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer)) {
                return;
            }
            // 阶段一占位：服务端将回送 HealthScreenSnapshot 摘要
        });
    }

    public static void handleStartTreatment(StartTreatmentRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer)) {
                return;
            }
            // 阶段一占位：重新验证距离/物品/目标/状态/并发后创建治疗会话
        });
    }

    public static void handleCancelTreatment(CancelTreatmentRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer)) {
                return;
            }
            // 阶段一占位：校验会话归属后取消
        });
    }

    public static void handleMedicalExamination(MedicalExaminationRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer)) {
                return;
            }
            // 阶段一占位：重新验证条件后执行检查并回送观察快照
        });
    }
}
