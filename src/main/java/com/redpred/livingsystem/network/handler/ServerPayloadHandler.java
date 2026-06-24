package com.redpred.livingsystem.network.handler;

import com.redpred.livingsystem.data.TreatmentDefinitionReloadListener;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.rule.definition.TreatmentDefinition;
import com.redpred.livingsystem.network.payload.CancelTreatmentRequest;
import com.redpred.livingsystem.network.payload.MedicalExaminationRequest;
import com.redpred.livingsystem.network.payload.OpenHealthScreenRequest;
import com.redpred.livingsystem.network.payload.StartTreatmentRequest;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.context.TreatmentContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端请求的服务端处理入口（见开发文档 §26）。
 *
 * <p>所有处理在逻辑主线程执行（{@link IPayloadContext#enqueueWork}），且服务端必须重新验证玩家身份、
 * 权限、距离、目标存在、物品槽位与会话状态，不信任客户端提交的数值与目标状态。治疗定义以服务端手持
 * 物品 ID 为准，不信任客户端提交的 {@code treatmentActionId}。</p>
 */
public final class ServerPayloadHandler {

    /** 队友治疗的最大距离平方（约 5 格）。 */
    private static final double TREAT_RANGE_SQR = 25.0;

    private ServerPayloadHandler() {
    }

    public static void handleOpenHealthScreen(OpenHealthScreenRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            PlayerHealthData data = LivingServices.REPOSITORY.get(player);
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                    com.redpred.livingsystem.network.snapshot.HealthScreenSnapshotFactory.build(data));
        });
    }

    public static void handleStartTreatment(StartTreatmentRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer practitioner)) {
                return;
            }
            ServerPlayer patient = payload.patientId().equals(practitioner.getUUID())
                    ? practitioner
                    : practitioner.server.getPlayerList().getPlayer(payload.patientId());
            if (patient == null) {
                return;
            }
            if (patient != practitioner && practitioner.distanceToSqr(patient) > TREAT_RANGE_SQR) {
                return;
            }
            // 以服务端手持物品为准确定治疗定义，不信任客户端提交的行为 ID。
            ItemStack stack = practitioner.getInventory().getItem(payload.itemSlot());
            if (stack.isEmpty()) {
                return;
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            TreatmentDefinition def = TreatmentDefinitionReloadListener.get(itemId);
            if (def == null || !def.enabled()) {
                return;
            }
            TreatmentContext treatmentContext = new TreatmentContext(
                    practitioner, patient, itemId, itemId, payload.targetEffectId(), null,
                    practitioner.level().getGameTime());
            if (LivingServices.TREATMENT.startTreatment(treatmentContext).isPresent() && !practitioner.isCreative()) {
                stack.shrink(1);
            }
        });
    }

    public static void handleCancelTreatment(CancelTreatmentRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            PlayerHealthData data = LivingServices.REPOSITORY.get(player);
            LivingServices.TREATMENT.cancel(player, data, payload.sessionId());
        });
    }

    public static void handleMedicalExamination(MedicalExaminationRequest payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer examiner)) {
                return;
            }
            ServerPlayer patient = payload.patientId().equals(examiner.getUUID())
                    ? examiner
                    : examiner.server.getPlayerList().getPlayer(payload.patientId());
            if (patient == null) {
                return;
            }
            if (patient != examiner && examiner.distanceToSqr(patient) > TREAT_RANGE_SQR) {
                return;
            }
            LivingServices.EXAMINATION.examine(examiner, patient, payload.examinationId()).ifPresent(obs -> {
                LivingServices.OBSERVATIONS.store(patient, obs);
                // 把测量结果以中文逐行反馈给检查者（精确数值属 MEASURED 级别）。
                obs.values().values().forEach(v ->
                        examiner.displayClientMessage(net.minecraft.network.chat.Component.literal("§b[检查] " + v.displayZhCn()), false));
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(examiner,
                        new com.redpred.livingsystem.network.payload.MedicalObservationPayload(obs.id(), obs.accuracy()));
            });
        });
    }
}
