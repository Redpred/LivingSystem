package com.redpred.livingsystem.domain.death;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

/**
 * 玩家本次死亡的医学与伤势原因摘要（见开发文档 §14.8）。
 *
 * <p>在进入原版死亡流程前由服务端生成；按因果关系回溯：终末生理失败 → 生理异常 → 伤势/健康影响
 * → 来源。不可变 {@code record}，作为持久化与网络快照。</p>
 */
public record DeathReportSnapshot(
        UUID reportId,
        UUID playerId,
        long deathGameTime,
        ResourceLocation primaryCauseId,
        ResourceLocation terminalFailureId,
        List<DeathContribution> majorContributions,
        List<DeathContribution> secondaryContributions,
        FinalVitalSnapshot finalVitals,
        List<ResourceLocation> appliedTreatments,
        List<DeathTimelineEntry> timeline
) {
}
