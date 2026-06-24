package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.data.TreatmentDefinitionReloadListener;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.BleedingState;
import com.redpred.livingsystem.domain.effect.ContaminationState;
import com.redpred.livingsystem.domain.effect.FractureState;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.treatment.AppliedTreatmentState;
import com.redpred.livingsystem.domain.treatment.TreatmentSession;
import com.redpred.livingsystem.rule.definition.TreatmentDefinition;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.context.TreatmentContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.redpred.livingsystem.network.payload.TreatmentProgressPayload;

/**
 * {@link TreatmentService} 默认实现（阶段三 3.2）。
 *
 * <p>服务端权威：按治疗定义校验并创建会话，逐刻推进进度，按 {@link TreatmentDefinition#commitPolicy()}
 * 把治疗定义中预注册的 {@code Operation} 施加到目标创伤的出血/凝血/污染/骨折等组件上，完成时记录
 * {@link AppliedTreatmentState}。需静止的治疗在患者移动时中断。进度只读同步给患者，客户端不得自行完成。</p>
 */
public final class DefaultTreatmentService implements TreatmentService {

    /** 判定"移动中"的水平速度平方阈值。 */
    private static final double MOVE_THRESHOLD_SQR = 0.02 * 0.02;

    @Override
    public Optional<TreatmentSession> startTreatment(TreatmentContext context) {
        ServerPlayer patient = context.patient();
        ServerPlayer practitioner = context.practitioner();
        TreatmentDefinition def = TreatmentDefinitionReloadListener.get(context.treatmentActionId());
        if (def == null || !def.enabled()) {
            return Optional.empty();
        }
        if (practitioner.getUUID().equals(patient.getUUID()) && !def.allowSelfTreatment()) {
            return Optional.empty();
        }
        PlayerHealthData data = LivingServices.REPOSITORY.get(patient);

        // 选择目标创伤：优先客户端指定，否则自动选最相关且最严重者。
        TraumaInjuryState target = resolveTarget(data, def, context.targetEffectId());
        if (target == null) {
            return Optional.empty();
        }
        // 并发：同一创伤的同一槽位若已有未完成会话，则拒绝重复发起。
        for (TreatmentSession existing : data.treatmentSessions().values()) {
            if (!existing.isCompleted() && target.id().equals(existing.getTargetEffectId())) {
                TreatmentDefinition existingDef = TreatmentDefinitionReloadListener.get(existing.getTreatmentActionId());
                if (existingDef != null && existingDef.slot() == def.slot()) {
                    return Optional.empty();
                }
            }
        }

        TreatmentSession session = new TreatmentSession(
                UUID.randomUUID(), practitioner.getUUID(), patient.getUUID(), def.id(), context.gameTime());
        session.setTargetEffectId(target.id());
        session.setTargetRegion(target.getBodyRegion());
        session.setSourceItemId(context.sourceItemId());
        data.treatmentSessions().put(session.getId(), session);
        sendProgress(patient, session, def, false);
        return Optional.of(session);
    }

    @Override
    public void tickSessions(ServerPlayer patient, PlayerHealthData data) {
        if (data.treatmentSessions().isEmpty()) {
            return;
        }
        boolean moving = patient.getDeltaMovement().horizontalDistanceSqr() > MOVE_THRESHOLD_SQR || !patient.onGround();
        List<UUID> finished = new ArrayList<>();
        for (TreatmentSession session : new ArrayList<>(data.treatmentSessions().values())) {
            if (session.isCompleted()) {
                finished.add(session.getId());
                continue;
            }
            TreatmentDefinition def = TreatmentDefinitionReloadListener.get(session.getTreatmentActionId());
            if (def == null) {
                finished.add(session.getId());
                continue;
            }
            if (def.requiresStationary() && moving) {
                session.setInterrupted(true);
                sendProgress(patient, session, def, true);
                finished.add(session.getId());
                continue;
            }

            float oldProgress = session.getProgress();
            float step = 1.0F / Math.max(1, def.durationTicks());
            float newProgress = Math.min(1.0F, oldProgress + step);
            float commitDelta = commitDelta(def, oldProgress, newProgress);
            if (commitDelta > 0.0F) {
                applyOperations(data, session, def, commitDelta);
            }
            session.setProgress(newProgress);
            session.setLastUpdatedGameTime(patient.level().getGameTime());

            if (newProgress >= 1.0F) {
                complete(data, session, def, patient.level().getGameTime());
                sendProgress(patient, session, def, false);
                finished.add(session.getId());
            } else {
                sendProgress(patient, session, def, false);
            }
        }
        finished.forEach(id -> data.treatmentSessions().remove(id));
    }

    @Override
    public boolean cancel(ServerPlayer patient, PlayerHealthData data, UUID sessionId) {
        TreatmentSession session = data.treatmentSessions().remove(sessionId);
        if (session == null) {
            return false;
        }
        session.setInterrupted(true);
        TreatmentDefinition def = TreatmentDefinitionReloadListener.get(session.getTreatmentActionId());
        sendProgress(patient, session, def, true);
        return true;
    }

    /** 选择目标创伤：指定 ID 优先且需相关；否则在适用创伤中选相关且最严重者。 */
    private static TraumaInjuryState resolveTarget(PlayerHealthData data, TreatmentDefinition def, UUID requested) {
        if (requested != null && !requested.equals(new UUID(0L, 0L))) {
            HealthEffectInstance effect = data.activeEffects().get(requested);
            if (effect instanceof TraumaInjuryState trauma && isRelevant(trauma, def)) {
                return trauma;
            }
        }
        TraumaInjuryState best = null;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof TraumaInjuryState trauma && isRelevant(trauma, def)
                    && (best == null || trauma.severity() > best.severity())) {
                best = trauma;
            }
        }
        return best;
    }

    /** 判断该治疗对某创伤是否有意义：创伤类型匹配，且存在对应可处理的组件状态。 */
    private static boolean isRelevant(TraumaInjuryState trauma, TreatmentDefinition def) {
        if (!def.applicableTraumaKinds().isEmpty() && !def.applicableTraumaKinds().contains(trauma.getTraumaKind())) {
            return false;
        }
        for (TreatmentDefinition.Operation op : def.operations()) {
            switch (op.type()) {
                case REDUCE_EXTERNAL_BLEEDING, INCREASE_CLOT_PROGRESS, APPLY_WOUND_CLOSURE -> {
                    if (trauma.getBleeding().isCurrentlyBleeding() || trauma.getBleeding().getBaseExternalRate() > 0.0F) {
                        return true;
                    }
                }
                case REDUCE_INTERNAL_BLEEDING -> {
                    if (trauma.getBleeding().getBaseInternalRate() > 0.0F) {
                        return true;
                    }
                }
                case REDUCE_CONTAMINATION -> {
                    if (trauma.getContamination().getContaminationLevel() > 0.0F) {
                        return true;
                    }
                }
                case STABILIZE_FRACTURE, REDUCE_FRACTURE_DISPLACEMENT -> {
                    if (trauma.getFracture().getGrade() > 0) {
                        return true;
                    }
                }
                default -> {
                }
            }
        }
        return false;
    }

    /** 按提交策略计算本刻应提交的进度增量。 */
    private static float commitDelta(TreatmentDefinition def, float oldProgress, float newProgress) {
        return switch (def.commitPolicy()) {
            case ON_COMPLETE -> (newProgress >= 1.0F && oldProgress < 1.0F) ? 1.0F : 0.0F;
            case PROPORTIONAL -> newProgress - oldProgress;
            case STAGED -> stage(newProgress) - stage(oldProgress);
        };
    }

    /** 分阶段提交：每跨越 0.25 提交一档。 */
    private static float stage(float progress) {
        return Mth.floor(progress * 4.0F) / 4.0F;
    }

    private static void applyOperations(PlayerHealthData data, TreatmentSession session, TreatmentDefinition def, float delta) {
        HealthEffectInstance effect = data.activeEffects().get(session.getTargetEffectId());
        if (!(effect instanceof TraumaInjuryState trauma)) {
            return;
        }
        for (TreatmentDefinition.Operation op : def.operations()) {
            applyOperation(trauma, op.type(), Mth.clamp(op.amount() * delta, 0.0F, 1.0F));
        }
    }

    /** 把单个预注册操作按缩放量施加到创伤组件（只动出血/凝血/污染/骨折等安全字段）。 */
    private static void applyOperation(TraumaInjuryState trauma, com.redpred.livingsystem.domain.treatment.TreatmentOperationType type, float scaled) {
        BleedingState b = trauma.getBleeding();
        FractureState f = trauma.getFracture();
        ContaminationState c = trauma.getContamination();
        switch (type) {
            case REDUCE_EXTERNAL_BLEEDING -> {
                b.setBaseExternalRate(b.getBaseExternalRate() * (1.0F - scaled));
                b.setRebleedRisk(b.getRebleedRisk() * (1.0F - scaled));
                if (scaled >= 0.99F) {
                    b.setCurrentlyBleeding(false);
                }
            }
            case REDUCE_INTERNAL_BLEEDING -> b.setBaseInternalRate(b.getBaseInternalRate() * (1.0F - scaled));
            case INCREASE_CLOT_PROGRESS -> {
                b.setClotProgress(Math.min(1.0F, b.getClotProgress() + scaled));
                if (b.getClotProgress() >= 1.0F) {
                    b.setCurrentlyBleeding(false);
                }
            }
            case REDUCE_CONTAMINATION -> {
                c.setContaminationLevel(c.getContaminationLevel() * (1.0F - scaled));
                if (c.getContaminationLevel() <= 0.01F) {
                    c.setCleaned(true);
                }
            }
            case APPLY_WOUND_CLOSURE -> b.setRebleedRisk(b.getRebleedRisk() * (1.0F - scaled));
            case STABILIZE_FRACTURE -> {
                f.setSplintStability(Math.min(1.0F, f.getSplintStability() + scaled));
                f.setInstability(f.getInstability() * (1.0F - scaled));
            }
            case REDUCE_FRACTURE_DISPLACEMENT -> {
                f.setInstability(f.getInstability() * (1.0F - scaled * 0.5F));
                if (scaled >= 0.5F) {
                    f.setDisplaced(false);
                    f.setReduced(true);
                }
            }
            default -> {
                // 输血/补液/镇痛/呼吸支持等系统性操作在后续子里程碑接入，此处安全忽略。
            }
        }
    }

    /** 完成治疗：记录已应用治疗状态到目标创伤。 */
    private static void complete(PlayerHealthData data, TreatmentSession session, TreatmentDefinition def, long gameTime) {
        session.setCompleted(true);
        HealthEffectInstance effect = data.activeEffects().get(session.getTargetEffectId());
        if (effect instanceof TraumaInjuryState trauma) {
            // 同槽位旧治疗失效，保留为历史但标记非活动。
            for (AppliedTreatmentState applied : trauma.getAppliedTreatments()) {
                if (applied.getSlot() == def.slot()) {
                    applied.setActive(false);
                }
            }
            AppliedTreatmentState applied = new AppliedTreatmentState(def.id(), def.slot(), gameTime);
            applied.setEffectiveness(1.0F);
            applied.setIntegrity(1.0F);
            applied.setRemainingDuration(-1L);
            trauma.getAppliedTreatments().add(applied);
        }
    }

    private static void sendProgress(ServerPlayer patient, TreatmentSession session, TreatmentDefinition def, boolean interrupted) {
        int duration = def != null ? def.durationTicks() : 0;
        int remaining = Math.max(0, Math.round((1.0F - session.getProgress()) * duration));
        PacketDistributor.sendToPlayer(patient,
                new TreatmentProgressPayload(session.getId(), session.getProgress(), remaining, interrupted));
    }
}
