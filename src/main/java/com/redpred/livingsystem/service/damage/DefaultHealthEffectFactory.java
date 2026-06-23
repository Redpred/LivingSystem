package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.data.DamageProfileReloadListener;
import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.effect.BleedingState;
import com.redpred.livingsystem.domain.effect.CauseSnapshot;
import com.redpred.livingsystem.domain.effect.FractureState;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.effect.TraumaKind;
import com.redpred.livingsystem.rule.definition.DamageProfile;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.hit.HitLocationResult;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * {@link HealthEffectFactory} 默认实现：按命中部位与伤害画像创建机械创伤 {@link TraumaInjuryState}。
 *
 * <p>2.2：固化外出血/深部内出血速率、动脉模式与再出血风险，交由生理循环逐 tick 处理。2.4：按伤害画像
 * 骨折概率（×全局倍率）做确定性判定，固化骨折等级/不稳定/移位并追加骨结构损伤。全身来源
 * （{@code region} 为空）不生成局部伤口。来源快照暂用 {@link CauseSnapshot#UNKNOWN}（细化留后续）。</p>
 */
public final class DefaultHealthEffectFactory implements HealthEffectFactory {

    @Override
    public List<HealthEffectInstance> create(DamageContext context, HitLocationResult location) {
        if (location.region().isEmpty()) {
            return List.of();
        }
        BodyRegion region = location.region().get();
        DamageProfile profile = DamageProfileReloadListener.get(context.source().getMsgId());

        TraumaKind kind = profile != null ? profile.traumaKind() : TraumaKind.CONTUSION;
        float base = profile != null ? profile.baseSeverity() : 0.3F;
        float painFactor = profile != null ? profile.painPerSeverity() : 0.6F;
        Map<AnatomicalStructure, Float> weights = (profile != null && !profile.structureWeights().isEmpty())
                ? profile.structureWeights()
                : Map.of(AnatomicalStructure.SOFT_TISSUE, 0.4F);

        float severity = Mth.clamp(base + context.amount() * 0.05F, 0.0F, 1.0F);

        TraumaInjuryState trauma = new TraumaInjuryState(
                UUID.randomUUID(), context.sourceEventId(), CauseSnapshot.UNKNOWN, region, kind, context.gameTime());
        trauma.setSeverity(severity);
        trauma.setDepth(severity);
        weights.forEach((structure, weight) -> trauma.getStructureDamage().put(structure, severity * weight));

        // 基础疼痛随严重度固化（创建时写入实例，不受后续定义修改影响，见 §12.1）；当前疼痛初值取基础疼痛，
        // 后续由活动加重、炎症与镇痛在生理循环中动态调整。
        float basePain = Mth.clamp(severity * painFactor, 0.0F, 1.0F);
        trauma.getPain().setBasePain(basePain);
        trauma.getPain().setCurrentPain(basePain);

        // 出血参数随严重度/深度固化（mL/分钟），由生理循环逐 tick 处理外出血、深部内出血、凝血与再出血。
        BleedingState bleeding = trauma.getBleeding();
        boolean arterial = severity > 0.7F;
        bleeding.setBaseExternalRate(severity * 600.0F);
        bleeding.setBaseInternalRate(severity * trauma.getDepth() * 300.0F);
        bleeding.setArterialPattern(arterial);
        bleeding.setRebleedRisk(arterial ? 0.4F : 0.15F);
        bleeding.setCurrentlyBleeding(severity > 0.05F);

        // 骨折判定（确定性随机，种子复用同一伤害事件，见 §5.6）：最终概率 = 画像骨折概率 × 全局倍率，夹取 0~1。
        float fractureChance = profile != null ? profile.fractureChance() : 0.0F;
        fractureChance = Mth.clamp(fractureChance * ModConfigs.FRACTURE_CHANCE_MULTIPLIER.get().floatValue(), 0.0F, 1.0F);
        if (fractureChance > 0.0F) {
            // 用 sourceEventId 低位与命中判定（用高位）区分通道，避免同一事件不同阶段随机相关。
            RandomSource random = RandomSource.create(context.sourceEventId().getLeastSignificantBits() ^ context.gameTime());
            if (random.nextFloat() < fractureChance) {
                int grade = severity >= 0.75F ? 3 : (severity >= 0.4F ? 2 : 1);
                boolean displaced = grade >= 2 && random.nextFloat() < 0.5F;
                FractureState fracture = trauma.getFracture();
                fracture.setGrade(grade);
                fracture.setInstability(Mth.clamp(severity * (displaced ? 1.0F : 0.7F), 0.0F, 1.0F));
                fracture.setDisplaced(displaced);
                // 骨折额外损伤骨结构完整度（与画像权重叠加），保证致死/结构判定可感知。
                trauma.getStructureDamage().merge(AnatomicalStructure.BONE, grade * 0.15F, Float::sum);
            }
        }

        return List.of(trauma);
    }
}
