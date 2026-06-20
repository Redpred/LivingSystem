package com.redpred.livingsystem.service.context;

import com.redpred.livingsystem.service.hit.HitEvidence;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.UUID;

/**
 * 一次伤害的瞬时上下文（见开发文档 §18 上下文层、§24.1）。
 *
 * <p>瞬时输入对象，允许持有运行时引用（{@link ServerPlayer}、{@link DamageSource}），不持久化。
 * {@code amount} 为原版与其他模组完成护甲/附魔/减伤结算后的最终有效伤害。</p>
 */
public record DamageContext(
        ServerPlayer victim,
        DamageSource source,
        float amount,
        UUID sourceEventId,
        long gameTime,
        HitEvidence hitEvidence
) {
}
