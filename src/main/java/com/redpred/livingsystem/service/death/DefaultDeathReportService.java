package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.death.DeathReportSnapshot;
import com.redpred.livingsystem.domain.death.FinalVitalSnapshot;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link DeathReportService} 默认实现。阶段二 2.1 生成基础死亡报告（主因/终末失败/最终体征）；
 * 完整因果链回溯随后续子里程碑充实。
 */
public final class DefaultDeathReportService implements DeathReportService {

    @Override
    public Optional<DeathReportSnapshot> buildReport(ServerPlayer player, PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float bloodFraction = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;
        FinalVitalSnapshot vitals = new FinalVitalSnapshot(
                bloodFraction, p.getConsciousness(), bloodFraction, 1.0F, p.getCoreTemperature());
        return Optional.of(new DeathReportSnapshot(
                UUID.randomUUID(),
                player.getUUID(),
                player.level().getGameTime(),
                ResourceLocation.fromNamespaceAndPath("livingsystem", "vital_failure"),
                ResourceLocation.fromNamespaceAndPath("livingsystem", "circulatory_collapse"),
                List.of(),
                List.of(),
                vitals,
                List.of(),
                List.of()));
    }
}
