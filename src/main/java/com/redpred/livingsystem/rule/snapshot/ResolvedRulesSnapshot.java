package com.redpred.livingsystem.rule.snapshot;

import com.redpred.livingsystem.rule.registry.DamageProfileRegistry;
import com.redpred.livingsystem.rule.registry.InjuryDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.PathogenDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.ProtectionDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.RadiationDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.SymptomDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.ToxinDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.TreatmentDefinitionRegistry;

/**
 * 运行时只读规则快照（见开发文档 §3.2.4、§22）。
 *
 * <p>配置和数据包加载完成后构建本快照，运行时系统只读取当前有效快照，不直接读取 TOML/JSON。
 * 重新加载时先完整构建新快照再原子替换；构建失败则保留上一份有效快照。</p>
 */
public final class ResolvedRulesSnapshot {

    private final FeaturePolicyRegistry featurePolicies;
    private final GlobalMultipliers globalMultipliers;
    private final DamageProfileRegistry damageProfiles;
    private final InjuryDefinitionRegistry injuryDefinitions;
    private final SymptomDefinitionRegistry symptomDefinitions;
    private final ToxinDefinitionRegistry toxinDefinitions;
    private final PathogenDefinitionRegistry pathogenDefinitions;
    private final RadiationDefinitionRegistry radiationDefinitions;
    private final TreatmentDefinitionRegistry treatmentDefinitions;
    private final ProtectionDefinitionRegistry protectionDefinitions;
    private final long version;

    public ResolvedRulesSnapshot(
            FeaturePolicyRegistry featurePolicies,
            GlobalMultipliers globalMultipliers,
            DamageProfileRegistry damageProfiles,
            InjuryDefinitionRegistry injuryDefinitions,
            SymptomDefinitionRegistry symptomDefinitions,
            ToxinDefinitionRegistry toxinDefinitions,
            PathogenDefinitionRegistry pathogenDefinitions,
            RadiationDefinitionRegistry radiationDefinitions,
            TreatmentDefinitionRegistry treatmentDefinitions,
            ProtectionDefinitionRegistry protectionDefinitions,
            long version) {
        this.featurePolicies = featurePolicies;
        this.globalMultipliers = globalMultipliers;
        this.damageProfiles = damageProfiles;
        this.injuryDefinitions = injuryDefinitions;
        this.symptomDefinitions = symptomDefinitions;
        this.toxinDefinitions = toxinDefinitions;
        this.pathogenDefinitions = pathogenDefinitions;
        this.radiationDefinitions = radiationDefinitions;
        this.treatmentDefinitions = treatmentDefinitions;
        this.protectionDefinitions = protectionDefinitions;
        this.version = version;
    }

    /** 空快照：无任何定义、全局倍率为默认、版本为 0。用于阶段一与构建失败回退。 */
    public static final ResolvedRulesSnapshot EMPTY = new ResolvedRulesSnapshot(
            FeaturePolicyRegistry.EMPTY,
            GlobalMultipliers.DEFAULT,
            new DamageProfileRegistry(),
            new InjuryDefinitionRegistry(),
            new SymptomDefinitionRegistry(),
            new ToxinDefinitionRegistry(),
            new PathogenDefinitionRegistry(),
            new RadiationDefinitionRegistry(),
            new TreatmentDefinitionRegistry(),
            new ProtectionDefinitionRegistry(),
            0L);

    public FeaturePolicyRegistry featurePolicies() { return featurePolicies; }
    public GlobalMultipliers globalMultipliers() { return globalMultipliers; }
    public DamageProfileRegistry damageProfiles() { return damageProfiles; }
    public InjuryDefinitionRegistry injuryDefinitions() { return injuryDefinitions; }
    public SymptomDefinitionRegistry symptomDefinitions() { return symptomDefinitions; }
    public ToxinDefinitionRegistry toxinDefinitions() { return toxinDefinitions; }
    public PathogenDefinitionRegistry pathogenDefinitions() { return pathogenDefinitions; }
    public RadiationDefinitionRegistry radiationDefinitions() { return radiationDefinitions; }
    public TreatmentDefinitionRegistry treatmentDefinitions() { return treatmentDefinitions; }
    public ProtectionDefinitionRegistry protectionDefinitions() { return protectionDefinitions; }
    public long version() { return version; }
}
