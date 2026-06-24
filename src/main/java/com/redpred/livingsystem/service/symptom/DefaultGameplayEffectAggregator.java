package com.redpred.livingsystem.service.symptom;

import com.redpred.livingsystem.domain.symptom.GameplayEffectSnapshot;
import com.redpred.livingsystem.domain.symptom.SymptomSnapshot;
import com.redpred.livingsystem.domain.symptom.SymptomState;
import net.minecraft.util.Mth;

/**
 * {@link GameplayEffectAggregator} 默认实现（阶段二 2.5）。
 *
 * <p>把症状强度汇总为唯一的游戏性输出并统一夹取上下限：失血性虚弱降低移动/攻速并加重镜头摇晃与心跳；
 * 腿部障碍大幅降低移动、重度时降低跳跃力并禁用疾跑；跛行（骨折）显著降低移动并禁用疾跑；
 * 臂部障碍降低攻速与挖掘速度并影响主手稳定；
 * 疼痛降低操作稳定度与镜头摇晃，高疼痛引发的颤抖进一步加重手部不稳与摇晃；
 * 寒战降低移动与操作稳定并加重摇晃，发热造成乏力（移动/攻速轻微下降）；昏迷则全面锁定操作。</p>
 */
public final class DefaultGameplayEffectAggregator implements GameplayEffectAggregator {

    @Override
    public GameplayEffectSnapshot aggregate(SymptomSnapshot symptoms) {
        float move = 1.0F;
        float jumpStrength = 1.0F;
        float attackSpeed = 1.0F;
        float mining = 1.0F;
        float mainHandStability = 1.0F;
        float offHandStability = 1.0F;
        boolean sprint = true;
        boolean jump = true;
        float sway = 0.0F;
        float heartbeat = 0.0F;
        boolean unconscious = false;

        for (SymptomState s : symptoms.symptoms()) {
            float i = s.intensity();
            switch (s.symptomId().getPath()) {
                case "hypovolemic_weakness" -> {
                    move *= 1.0F - 0.3F * i;
                    attackSpeed *= 1.0F - 0.3F * i;
                    sway += 0.3F * i;
                    heartbeat += i;
                    if (i > 0.8F) {
                        sprint = false;
                    }
                }
                case "leg_impairment" -> {
                    move *= 1.0F - 0.5F * i;
                    jumpStrength *= 1.0F - 0.6F * i;
                    if (i > 0.7F) {
                        sprint = false;
                        jump = false;
                    }
                }
                case "limp" -> {
                    // 跛行：移动显著下降并禁止疾跑，重度时进一步压低跳跃力。
                    move *= 1.0F - 0.45F * i;
                    sprint = false;
                    if (i > 0.6F) {
                        jumpStrength *= 1.0F - 0.5F * i;
                    }
                }
                case "arm_impairment" -> {
                    attackSpeed *= 1.0F - 0.4F * i;
                    mining *= 1.0F - 0.5F * i;
                    mainHandStability *= 1.0F - 0.5F * i;
                }
                case "pain" -> {
                    move *= 1.0F - 0.1F * i;
                    attackSpeed *= 1.0F - 0.1F * i;
                    mainHandStability *= 1.0F - 0.3F * i;
                    offHandStability *= 1.0F - 0.3F * i;
                    sway += 0.2F * i;
                }
                case "tremor" -> {
                    mainHandStability *= 1.0F - 0.5F * i;
                    offHandStability *= 1.0F - 0.4F * i;
                    sway += 0.4F * i;
                }
                case "unconsciousness" -> {
                    // 昏迷：大幅压制移动与攻击并禁用疾跑/跳跃；强度足够时置 unconscious 锁定。
                    move *= 1.0F - 0.9F * i;
                    attackSpeed *= 1.0F - 0.9F * i;
                    sprint = false;
                    jump = false;
                    if (i >= 0.5F) {
                        unconscious = true;
                    }
                }
                case "chills" -> {
                    // 寒战：发抖导致移动下降、操作不稳与镜头摇晃。
                    move *= 1.0F - 0.25F * i;
                    mainHandStability *= 1.0F - 0.3F * i;
                    sway += 0.2F * i;
                }
                case "fever" -> {
                    // 发热：虚弱乏力，移动与攻速轻微下降。
                    move *= 1.0F - 0.2F * i;
                    attackSpeed *= 1.0F - 0.2F * i;
                }
                default -> {
                }
            }
        }

        return new GameplayEffectSnapshot(
                Mth.clamp(move, 0.1F, 1.0F),
                Mth.clamp(jumpStrength, 0.1F, 1.0F),
                Mth.clamp(attackSpeed, 0.1F, 1.0F),
                Mth.clamp(mining, 0.1F, 1.0F),
                Mth.clamp(mainHandStability, 0.1F, 1.0F),
                Mth.clamp(offHandStability, 0.1F, 1.0F),
                sprint,
                jump,
                Mth.clamp(sway, 0.0F, 1.0F),
                0.0F,
                0.0F,
                0.0F,
                Mth.clamp(heartbeat, 0.0F, 1.0F),
                unconscious);
    }
}
