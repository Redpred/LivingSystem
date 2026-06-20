package com.redpred.livingsystem.service.treatment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * {@link ConsumableEffectService} 默认实现。阶段一不应用任何摄入效果。
 */
public final class DefaultConsumableEffectService implements ConsumableEffectService {

    @Override
    public void applyConsumable(ServerPlayer player, ItemStack stack) {
        // 阶段一占位
    }
}
