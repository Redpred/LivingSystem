package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.body.StructureState;
import com.redpred.livingsystem.domain.death.DeathContribution;
import com.redpred.livingsystem.domain.death.DeathReportSnapshot;
import com.redpred.livingsystem.domain.death.DeathTimelineEntry;
import com.redpred.livingsystem.domain.death.FinalVitalSnapshot;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link DeathReportService} 默认实现（阶段三 3.6）。
 *
 * <p>在进入原版死亡流程前按因果关系回溯死亡原因：先判定终末失败类型（失血性休克/缺氧/心脏停搏/
 * 神经衰竭），再列出严重伤势作为主要促成因素，并记录最终体征与时间线。完整多系统因果链随后续系统扩充。</p>
 */
public final class DefaultDeathReportService implements DeathReportService {

    @Override
    public Optional<DeathReportSnapshot> buildReport(ServerPlayer player, PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float bloodFraction = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;

        ResourceLocation primary;
        ResourceLocation terminal;
        String terminalDesc;
        if (bloodFraction < ModConfigs.UNSURVIVABLE_BLOOD_FRACTION.get().floatValue()) {
            primary = rl("hemorrhage");
            terminal = rl("circulatory_collapse");
            terminalDesc = "失血性休克";
        } else if (p.getOxygenDebt() >= 1.0F) {
            primary = rl("asphyxia");
            terminal = rl("respiratory_failure");
            terminalDesc = "缺氧窒息";
        } else if (structureDestroyed(data, BodyRegion.CHEST, AnatomicalStructure.HEART)) {
            primary = rl("cardiac_injury");
            terminal = rl("cardiac_arrest");
            terminalDesc = "心脏停搏";
        } else if (structureDestroyed(data, BodyRegion.HEAD_NECK, AnatomicalStructure.BRAIN)) {
            primary = rl("brain_injury");
            terminal = rl("neurological_failure");
            terminalDesc = "中枢神经衰竭";
        } else {
            primary = rl("vital_failure");
            terminal = rl("circulatory_collapse");
            terminalDesc = "生理崩溃";
        }

        List<DeathContribution> major = new ArrayList<>();
        List<ResourceLocation> treatments = new ArrayList<>();
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof TraumaInjuryState trauma && trauma.severity() > 0.3F) {
                major.add(new DeathContribution(
                        rl(trauma.getTraumaKind().name().toLowerCase()),
                        Optional.of(trauma.id()),
                        trauma.severity(),
                        regionName(trauma.getBodyRegion()) + " " + trauma.getTraumaKind().name()));
                trauma.getAppliedTreatments().stream()
                        .filter(a -> a.isActive() && a.getTreatmentId() != null)
                        .forEach(a -> treatments.add(a.getTreatmentId()));
            }
        }

        FinalVitalSnapshot vitals = new FinalVitalSnapshot(
                bloodFraction, p.getConsciousness(), bloodFraction,
                1.0F - p.getOxygenDebt(), p.getCoreTemperature());
        List<DeathTimelineEntry> timeline = List.of(
                new DeathTimelineEntry(player.level().getGameTime(), "死亡：" + terminalDesc));

        return Optional.of(new DeathReportSnapshot(
                UUID.randomUUID(), player.getUUID(), player.level().getGameTime(),
                primary, terminal, major, List.of(), vitals, treatments, timeline));
    }

    private static boolean structureDestroyed(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure) {
        BodyRegionState regionState = data.bodyRegions().get(region);
        if (regionState == null) {
            return false;
        }
        StructureState state = regionState.structure(structure);
        return state != null && state.getIntegrity() <= 0.0F;
    }

    private static String regionName(BodyRegion region) {
        return switch (region) {
            case HEAD_NECK -> "头部";
            case CHEST -> "胸腔";
            case ABDOMEN -> "腹部";
            case LEFT_ARM -> "左臂";
            case RIGHT_ARM -> "右臂";
            case LEFT_LEG -> "左腿";
            case RIGHT_LEG -> "右腿";
        };
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath("livingsystem", path);
    }
}
