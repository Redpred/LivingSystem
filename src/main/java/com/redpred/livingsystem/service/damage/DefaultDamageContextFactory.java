package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.hit.HitEvidence;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.UUID;

/**
 * {@link DamageContextFactory} 默认实现。
 *
 * <p>阶段一以随机 UUID 作为 {@code sourceEventId} 占位；后续阶段改为由“玩家 UUID + 事件 + 游戏时间 +
 * DamageType”派生的确定性种子（见开发文档 §6.6），并填充 {@link HitEvidence}。</p>
 */
public final class DefaultDamageContextFactory implements DamageContextFactory {

    @Override
    public DamageContext create(ServerPlayer victim, DamageSource source, float amount, long gameTime) {
        return new DamageContext(victim, source, amount, UUID.randomUUID(), gameTime, HitEvidence.NONE);
    }
}
