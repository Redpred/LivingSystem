package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.death.DeathReportSnapshot;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * 死亡报告服务（见开发文档 §14.8、§23）。在进入原版死亡流程前构建、保存并同步死亡报告，
 * 按因果关系回溯主要死亡原因与促进因素。
 */
public interface DeathReportService {

    /** 构建本次死亡的报告快照。 */
    Optional<DeathReportSnapshot> buildReport(ServerPlayer player, PlayerHealthData data);
}
