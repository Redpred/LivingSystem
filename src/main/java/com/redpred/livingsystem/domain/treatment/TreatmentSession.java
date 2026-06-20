package com.redpred.livingsystem.domain.treatment;

import com.redpred.livingsystem.domain.body.BodyRegion;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 玩家正在执行的一次治疗操作（见开发文档 §13.5）。
 *
 * <p>治疗是一个持续过程，由服务端创建和推进；客户端只接收用于显示的治疗进度。可变运行时状态。</p>
 */
public final class TreatmentSession {
    /** 本次治疗过程的唯一标识。 */
    private final UUID id;
    /** 执行治疗的实体。 */
    private UUID practitionerId;
    /** 接受治疗的玩家。 */
    private UUID patientId;
    /** 被治疗的具体健康影响实例，可为空。 */
    private UUID targetEffectId;
    /** 被治疗的身体部位，可为空。 */
    private BodyRegion targetRegion;
    /** 使用的治疗行为定义。 */
    private ResourceLocation treatmentActionId;
    /** 使用的物品类型。 */
    private ResourceLocation sourceItemId;
    /** 已完成的治疗进度，0.0～1.0。 */
    private float progress;
    /** 开始治疗的游戏时间。 */
    private final long startedGameTime;
    /** 上次更新治疗进度的游戏时间。 */
    private long lastUpdatedGameTime;
    /** 治疗是否已被中断。 */
    private boolean interrupted;
    /** 治疗是否已完成。 */
    private boolean completed;

    public TreatmentSession(UUID id, UUID practitionerId, UUID patientId,
                            ResourceLocation treatmentActionId, long startedGameTime) {
        this.id = id;
        this.practitionerId = practitionerId;
        this.patientId = patientId;
        this.treatmentActionId = treatmentActionId;
        this.startedGameTime = startedGameTime;
        this.lastUpdatedGameTime = startedGameTime;
    }

    public UUID getId() { return id; }
    public UUID getPractitionerId() { return practitionerId; }
    public void setPractitionerId(UUID v) { this.practitionerId = v; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID v) { this.patientId = v; }
    public UUID getTargetEffectId() { return targetEffectId; }
    public void setTargetEffectId(UUID v) { this.targetEffectId = v; }
    public BodyRegion getTargetRegion() { return targetRegion; }
    public void setTargetRegion(BodyRegion v) { this.targetRegion = v; }
    public ResourceLocation getTreatmentActionId() { return treatmentActionId; }
    public void setTreatmentActionId(ResourceLocation v) { this.treatmentActionId = v; }
    public ResourceLocation getSourceItemId() { return sourceItemId; }
    public void setSourceItemId(ResourceLocation v) { this.sourceItemId = v; }
    public float getProgress() { return progress; }
    public void setProgress(float v) { this.progress = v; }
    public long getStartedGameTime() { return startedGameTime; }
    public long getLastUpdatedGameTime() { return lastUpdatedGameTime; }
    public void setLastUpdatedGameTime(long v) { this.lastUpdatedGameTime = v; }
    public boolean isInterrupted() { return interrupted; }
    public void setInterrupted(boolean v) { this.interrupted = v; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean v) { this.completed = v; }
}
