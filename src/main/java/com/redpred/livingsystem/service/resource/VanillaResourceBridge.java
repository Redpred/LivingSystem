package com.redpred.livingsystem.service.resource;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.service.context.DamageContext;
import net.minecraft.server.level.ServerPlayer;

/**
 * 原版资源桥接（见开发文档 §7.6、§17 不变量）。集中处理原版生命、饥饿、回血与溺水的取消/转换，
 * 把捕获的伤害转换为 LivingSystem 健康影响，并提供死亡重入保护。
 */
public interface VanillaResourceBridge {

    /** 该玩家当前是否处于 LivingSystem 死亡处理流程中（重入保护）。 */
    boolean isHandlingDeath(ServerPlayer player);

    /** 标记开始死亡处理，避免专用致死来源再次被解析成新伤口。 */
    void beginDeathHandling(ServerPlayer player);

    /** 标记结束死亡处理。 */
    void endDeathHandling(ServerPlayer player);

    /**
     * 处理一次被捕获的玩家受伤：解析命中部位、生成创伤伤势、施加结构损伤与急性失血，写入聚合根。
     * 不在此扣减原版生命（由拦截器阻止原版结算）。
     */
    void handleIncoming(ServerPlayer player, DamageContext context);

    /** 将原版生命/饥饿等资源钉在安全哨兵值，使其不再作为权威生命资源。 */
    void syncVanillaResources(ServerPlayer player, PlayerHealthData data);
}
