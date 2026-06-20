package com.redpred.livingsystem.service.resource;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.server.level.ServerPlayer;

/**
 * 原版资源桥接（见开发文档 §7.6、§17 不变量）。集中处理原版生命、饥饿、回血与溺水的取消、转换，
 * 以及死亡重入保护，禁止分散在多个监听器中。
 *
 * <p>阶段一只建立接口与重入保护骨架，<b>不</b>启用最终伤害替换逻辑（见阶段任务 12）。</p>
 */
public interface VanillaResourceBridge {

    /** 该玩家当前是否处于 LivingSystem 死亡处理流程中（重入保护）。 */
    boolean isHandlingDeath(ServerPlayer player);

    /** 标记开始死亡处理，避免专用致死来源再次被解析成新伤口。 */
    void beginDeathHandling(ServerPlayer player);

    /** 标记结束死亡处理。 */
    void endDeathHandling(ServerPlayer player);

    /** 将原版生命/饥饿/空气等资源镜像为 LivingSystem 的权威状态（哨兵值）。 */
    void syncVanillaResources(ServerPlayer player, PlayerHealthData data);
}
