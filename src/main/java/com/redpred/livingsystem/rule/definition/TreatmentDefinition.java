package com.redpred.livingsystem.rule.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpred.livingsystem.domain.effect.TraumaKind;
import com.redpred.livingsystem.domain.treatment.TreatmentCommitPolicy;
import com.redpred.livingsystem.domain.treatment.TreatmentOperationType;
import com.redpred.livingsystem.domain.treatment.TreatmentPurpose;
import com.redpred.livingsystem.domain.treatment.TreatmentSlot;
import com.redpred.livingsystem.domain.treatment.TreatmentTargetType;
import com.redpred.livingsystem.rule.codec.EnumCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 治疗行为定义（见开发文档 §13.16 的 {@code TreatmentActionDefinition}）。
 *
 * <p>数据驱动：每个定义描述一种治疗行为的目标类型、目的、占用槽位、提交方式、所需时长、约束（需静止/
 * 可自疗）、适用的创伤类型，以及一组预注册的安全操作（{@link Operation}）。治疗完成或推进时，服务只能
 * 执行这些预注册操作，不得通过任意字段路径修改健康数据（见 §13.16 安全约束）。</p>
 *
 * @param id                  定义 ID（约定与触发治疗的医疗物品同 ID）
 * @param descriptionZhCn     中文说明
 * @param enabled             是否启用
 * @param targetType          治疗目标类型
 * @param purpose             治疗目的
 * @param slot                占用的功能槽位
 * @param commitPolicy        效果提交方式
 * @param durationTicks       完成所需游戏刻
 * @param requiresStationary  执行期间是否需要保持静止
 * @param allowSelfTreatment  是否允许对自己施用
 * @param applicableTraumaKinds 适用的创伤类型（为空表示不限）
 * @param operations          完成/推进时允许执行的预注册操作
 */
public record TreatmentDefinition(
        ResourceLocation id,
        String descriptionZhCn,
        boolean enabled,
        TreatmentTargetType targetType,
        TreatmentPurpose purpose,
        TreatmentSlot slot,
        TreatmentCommitPolicy commitPolicy,
        int durationTicks,
        boolean requiresStationary,
        boolean allowSelfTreatment,
        List<TraumaKind> applicableTraumaKinds,
        List<Operation> operations
) implements RuleDefinition {

    /**
     * 单个治疗操作：预注册的安全操作类型与作用量（0~1 归一化，具体语义由操作类型决定）。
     */
    public record Operation(TreatmentOperationType type, float amount) {
        public static final Codec<Operation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EnumCodecs.of(TreatmentOperationType.class).fieldOf("type").forGetter(Operation::type),
                Codec.FLOAT.optionalFieldOf("amount", 1.0F).forGetter(Operation::amount)
        ).apply(instance, Operation::new));
    }

    public static final Codec<TreatmentDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(TreatmentDefinition::id),
            Codec.STRING.optionalFieldOf("description_zh_cn", "").forGetter(TreatmentDefinition::descriptionZhCn),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(TreatmentDefinition::enabled),
            EnumCodecs.of(TreatmentTargetType.class).optionalFieldOf("target_type", TreatmentTargetType.INJURY_INSTANCE)
                    .forGetter(TreatmentDefinition::targetType),
            EnumCodecs.of(TreatmentPurpose.class).optionalFieldOf("purpose", TreatmentPurpose.CAUSAL)
                    .forGetter(TreatmentDefinition::purpose),
            EnumCodecs.of(TreatmentSlot.class).optionalFieldOf("slot", TreatmentSlot.WOUND_COVER)
                    .forGetter(TreatmentDefinition::slot),
            EnumCodecs.of(TreatmentCommitPolicy.class).optionalFieldOf("commit_policy", TreatmentCommitPolicy.ON_COMPLETE)
                    .forGetter(TreatmentDefinition::commitPolicy),
            Codec.INT.optionalFieldOf("duration_ticks", 40).forGetter(TreatmentDefinition::durationTicks),
            Codec.BOOL.optionalFieldOf("requires_stationary", true).forGetter(TreatmentDefinition::requiresStationary),
            Codec.BOOL.optionalFieldOf("allow_self_treatment", true).forGetter(TreatmentDefinition::allowSelfTreatment),
            EnumCodecs.of(TraumaKind.class).listOf().optionalFieldOf("applicable_trauma_kinds", List.of())
                    .forGetter(TreatmentDefinition::applicableTraumaKinds),
            Operation.CODEC.listOf().optionalFieldOf("operations", List.of())
                    .forGetter(TreatmentDefinition::operations)
    ).apply(instance, TreatmentDefinition::new));
}
