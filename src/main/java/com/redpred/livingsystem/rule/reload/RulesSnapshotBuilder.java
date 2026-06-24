package com.redpred.livingsystem.rule.reload;

import com.redpred.livingsystem.rule.definition.DamageProfile;
import com.redpred.livingsystem.rule.definition.TreatmentDefinition;
import com.redpred.livingsystem.rule.registry.DamageProfileRegistry;
import com.redpred.livingsystem.rule.registry.InjuryDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.PathogenDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.ProtectionDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.RadiationDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.SymptomDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.ToxinDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.TreatmentDefinitionRegistry;
import com.redpred.livingsystem.rule.snapshot.FeaturePolicyRegistry;
import com.redpred.livingsystem.rule.snapshot.GlobalMultipliers;
import com.redpred.livingsystem.rule.snapshot.ResolvedRulesSnapshot;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 规则快照共享构建器（见开发文档 §22 配置重载流程）。
 *
 * <p>不同数据包目录由各自的重载监听器加载；它们把各自解析出的定义写入本构建器的对应分区，
 * 每次写入后重建一份完整 {@link ResolvedRulesSnapshot} 并原子装入 {@link RulesReloadManager}。
 * 这样多个监听器在同一次数据包重载中协作装配，互不覆盖（同一次重载中后写入者只补充自己分区，
 * 重建时合并全部当前分区）。各分区构建失败时由对应监听器跳过，不写入本构建器，从而保留上一份有效值。</p>
 */
public final class RulesSnapshotBuilder {

    private static volatile Map<ResourceLocation, DamageProfile> damageProfiles = Map.of();
    private static volatile Map<ResourceLocation, TreatmentDefinition> treatments = Map.of();
    private static final AtomicLong VERSION = new AtomicLong();

    private RulesSnapshotBuilder() {
    }

    /** 设置伤害画像分区并重建装入快照。 */
    public static synchronized void setDamageProfiles(Map<ResourceLocation, DamageProfile> profiles) {
        damageProfiles = Map.copyOf(profiles);
        rebuildAndInstall();
    }

    /** 设置治疗定义分区并重建装入快照。 */
    public static synchronized void setTreatments(Map<ResourceLocation, TreatmentDefinition> definitions) {
        treatments = Map.copyOf(definitions);
        rebuildAndInstall();
    }

    /** 重置所有分区（用于服务器停止/重置）。 */
    public static synchronized void reset() {
        damageProfiles = Map.of();
        treatments = Map.of();
    }

    private static void rebuildAndInstall() {
        ResolvedRulesSnapshot snapshot = new ResolvedRulesSnapshot(
                FeaturePolicyRegistry.EMPTY,
                GlobalMultipliers.DEFAULT,
                new DamageProfileRegistry(damageProfiles),
                new InjuryDefinitionRegistry(),
                new SymptomDefinitionRegistry(),
                new ToxinDefinitionRegistry(),
                new PathogenDefinitionRegistry(),
                new RadiationDefinitionRegistry(),
                new TreatmentDefinitionRegistry(treatments),
                new ProtectionDefinitionRegistry(),
                VERSION.incrementAndGet());
        RulesReloadManager.install(snapshot);
    }
}
