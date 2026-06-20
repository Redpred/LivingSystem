package com.redpred.livingsystem.service.resource;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link VanillaResourceBridge} 默认实现。
 *
 * <p>阶段一仅提供死亡重入保护标记，不改变任何原版资源行为；最终伤害替换在后续阶段启用。</p>
 */
public final class DefaultVanillaResourceBridge implements VanillaResourceBridge {

    /** 正在进行 LivingSystem 死亡处理的玩家集合。 */
    private final Set<UUID> handlingDeath = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isHandlingDeath(ServerPlayer player) {
        return handlingDeath.contains(player.getUUID());
    }

    @Override
    public void beginDeathHandling(ServerPlayer player) {
        handlingDeath.add(player.getUUID());
    }

    @Override
    public void endDeathHandling(ServerPlayer player) {
        handlingDeath.remove(player.getUUID());
    }

    @Override
    public void syncVanillaResources(ServerPlayer player, PlayerHealthData data) {
        // 阶段一占位：不接管原版资源
    }
}
