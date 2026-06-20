package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.death.DeathReportSnapshot;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * {@link DeathReportService} 默认实现。阶段一不生成死亡报告。
 */
public final class DefaultDeathReportService implements DeathReportService {

    @Override
    public Optional<DeathReportSnapshot> buildReport(ServerPlayer player, PlayerHealthData data) {
        return Optional.empty();
    }
}
