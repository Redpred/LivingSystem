package com.redpred.livingsystem.service.physiology;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.BleedingState;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.PainState;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.physiology.ActivitySnapshot;
import com.redpred.livingsystem.domain.physiology.DerivedVitals;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.NeoForgeMod;

/**
 * {@link PhysiologyEngine} 默认实现（阶段二 2.2）。
 *
 * <p>{@link #runCycle} 逐周期推进每个伤口的外出血、深部内出血、凝血（动脉更难凝）、凝块稳定化与再出血。
 * 局部功能与失血造成的"伤退"已移交症状/游戏性输出链（见 {@code SymptomEngine}/{@code GameplayEffectAggregator}），
 * 不再在此施加状态效果。{@link #computeVitals} 由血容量推导基础灌注与休克。</p>
 */
public final class DefaultPhysiologyEngine implements PhysiologyEngine {

    private static final float CLOT_PER_CYCLE = 0.02F;
    private static final float STABILIZE_PER_CYCLE = 0.05F;

    @Override
    public void runCycle(ServerPlayer player, PlayerHealthData data, ActivitySnapshot activity) {
        PhysiologyState physiology = data.physiology();
        int interval = ModConfigs.TICK_INTERVAL.get();
        float minutesPerCycle = interval / 1200.0F; // 1200 ticks ≈ 1 分钟
        float clotBase = CLOT_PER_CYCLE * Math.max(0.1F, physiology.getBaselineClottingEfficiency());
        RandomSource random = player.getRandom();
        // 活动加重系数：剧烈活动（冲刺/跳跃/游泳）加重疼痛，静止为基准（活动接入细化见 §6.4）。
        float activityPainFactor = (activity.sprinting() || activity.jumping() || activity.swimming()) ? 1.25F : 1.0F;

        float totalLoss = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (!(effect instanceof TraumaInjuryState trauma)) {
                continue;
            }
            BleedingState b = trauma.getBleeding();
            boolean hasInternal = b.getBaseInternalRate() > 0.0F;

            if (b.getClotProgress() < 1.0F && (b.isCurrentlyBleeding() || hasInternal)) {
                float clotStep = clotBase * (b.isArterialPattern() ? 0.5F : 1.0F);
                b.setClotProgress(Math.min(1.0F, b.getClotProgress() + clotStep));
            }

            if (b.isCurrentlyBleeding()) {
                totalLoss += b.getBaseExternalRate() * (1.0F - b.getClotProgress()) * minutesPerCycle;
                if (b.getClotProgress() >= 1.0F) {
                    b.setCurrentlyBleeding(false);
                }
            } else if (b.getClotStability() < 1.0F) {
                b.setClotStability(Math.min(1.0F, b.getClotStability() + STABILIZE_PER_CYCLE));
                if (random.nextFloat() < b.getRebleedRisk() * (1.0F - b.getClotStability()) * 0.2F) {
                    b.setCurrentlyBleeding(true);
                    b.setClotProgress(Math.max(0.0F, b.getClotProgress() - 0.4F));
                }
            }

            if (hasInternal) {
                totalLoss += b.getBaseInternalRate() * (1.0F - b.getClotProgress()) * minutesPerCycle;
            }

            // 疼痛：当前疼痛朝 基础疼痛×活动加重 平滑回归，避免阈值附近突变。
            PainState pain = trauma.getPain();
            float painTarget = Mth.clamp(pain.getBasePain() * activityPainFactor, 0.0F, 1.0F);
            pain.setCurrentPain(Mth.lerp(0.25F, pain.getCurrentPain(), painTarget));
        }
        if (totalLoss > 0.0F) {
            physiology.setCurrentBloodVolume(Math.max(0.0F, physiology.getCurrentBloodVolume() - totalLoss));
        }

        updateRespiration(player, physiology, interval);
        updateMetabolism(physiology, activity, interval);
        updateStaminaAndConsciousness(physiology, activity, interval);
    }

    /**
     * 基础代谢：水分、营养与代谢能量随时间缓慢消耗，活动加速消耗。靠摄入物补充（见 {@code ConsumableEffectService}）。
     * 阶段三不致死（脱水/饥饿致死属阶段五代谢系统）；资源耗尽只限制体力恢复。
     */
    private static void updateMetabolism(PhysiologyState p, ActivitySnapshot activity, int interval) {
        float perMinute = interval / 1200.0F;
        float activityMul = (activity.sprinting() || activity.swimming()) ? 2.0F : (activity.walking() ? 1.3F : 1.0F);
        p.setHydration(Math.max(0.0F, p.getHydration() - 8.0F * perMinute * activityMul));
        p.setNutrition(Math.max(0.0F, p.getNutrition() - 5.0F * perMinute * activityMul));
        p.setMetabolicEnergy(Math.max(0.0F, p.getMetabolicEnergy() - 10.0F * perMinute * activityMul));
    }

    /**
     * 呼吸储备与氧债：水下且无水下呼吸能力时呼吸储备下降、耗尽后累积氧债；可正常呼吸时储备回升并清偿氧债。
     * 取代原版溺水扣血（原版溺水伤害已在拦截层清零，见 §7.5）。
     */
    private static void updateRespiration(ServerPlayer player, PhysiologyState physiology, int interval) {
        float perSecond = interval / 20.0F;
        boolean inWater = player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value());
        boolean canBreathe = player.hasEffect(MobEffects.WATER_BREATHING) || player.hasEffect(MobEffects.CONDUIT_POWER);
        boolean suffocating = inWater && !canBreathe;
        if (suffocating) {
            physiology.setRespiratoryReserve(Math.max(0.0F, physiology.getRespiratoryReserve() - 0.15F * perSecond));
            if (physiology.getRespiratoryReserve() <= 0.0F) {
                physiology.setOxygenDebt(Math.min(1.0F, physiology.getOxygenDebt() + 0.12F * perSecond));
            }
        } else {
            physiology.setRespiratoryReserve(Math.min(1.0F, physiology.getRespiratoryReserve() + 0.25F * perSecond));
            if (physiology.getOxygenDebt() > 0.0F) {
                physiology.setOxygenDebt(Math.max(0.0F, physiology.getOxygenDebt() - 0.2F * perSecond));
            }
        }
    }

    /**
     * 体力与意识：剧烈活动消耗体力、静止恢复；意识朝由氧债与低灌注决定的目标平滑回归（昏迷的完整输入
     * 锁定在阶段三处理，此处仅维护意识数值，供死亡判定与后续昏迷使用）。
     */
    private static void updateStaminaAndConsciousness(PhysiologyState physiology, ActivitySnapshot activity, int interval) {
        float perSecond = interval / 20.0F;
        float stamina = physiology.getCurrentStamina();
        float drain = 0.0F;
        if (activity.sprinting()) {
            drain += 4.0F;
        }
        if (activity.swimming()) {
            drain += 3.0F;
        }
        if (activity.jumping()) {
            drain += 2.0F;
        }
        if (drain > 0.0F) {
            stamina -= drain * perSecond;
        } else if (!activity.walking() && physiology.getMetabolicEnergy() > 0.0F && physiology.getHydration() > 0.0F) {
            stamina += 3.0F * perSecond;
        }
        physiology.setCurrentStamina(Mth.clamp(stamina, 0.0F, physiology.getMaxStamina()));

        float perfusion = physiology.getMaxBloodVolume() > 0
                ? physiology.getCurrentBloodVolume() / physiology.getMaxBloodVolume() : 1.0F;
        float consTarget = Mth.clamp(
                1.0F - physiology.getOxygenDebt() - Math.max(0.0F, 0.45F - perfusion), 0.0F, 1.0F);
        physiology.setConsciousness(Mth.lerp(0.2F, physiology.getConsciousness(), consTarget));
    }

    @Override
    public DerivedVitals computeVitals(PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float perfusion = p.getMaxBloodVolume() > 0
                ? Mth.clamp(p.getCurrentBloodVolume() / p.getMaxBloodVolume(), 0.0F, 1.0F) : 1.0F;
        float totalPain = aggregateTotalPain(data, p);
        return new DerivedVitals(0, 0, perfusion, 0, 0, 0, 0, totalPain, 1.0F - perfusion, 0, 0, 0, 0);
    }

    /**
     * 汇总全身总疼痛：各创伤当前疼痛求和（SUM_CLAMPED），乘全局疼痛倍率后扣除镇痛强度，夹取 0~1。
     */
    public static float aggregateTotalPain(PlayerHealthData data, PhysiologyState physiology) {
        float sum = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof TraumaInjuryState trauma) {
                sum += trauma.getPain().getCurrentPain();
            }
        }
        float painMult = ModConfigs.PAIN_INTENSITY_MULTIPLIER.get().floatValue();
        return Mth.clamp(sum * painMult - physiology.getAnalgesiaLevel(), 0.0F, 1.0F);
    }
}
