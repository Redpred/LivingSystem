package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.service.context.DamageContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

/**
 * 从伤害事件构建统一 {@link DamageContext}（见开发文档 §23、§24.1）。
 */
public interface DamageContextFactory {

    /** 在原版完成减伤结算后，构建本次伤害的统一上下文。 */
    DamageContext create(ServerPlayer victim, DamageSource source, float amount, long gameTime);
}
