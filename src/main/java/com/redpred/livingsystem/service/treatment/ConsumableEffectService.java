package com.redpred.livingsystem.service.treatment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 摄入物效果服务（见开发文档 §14.1、§24.4）。在物品成功完成食用/饮用后追加配置的健康与治疗效果，
 * 不接管其他模组的右键逻辑与容器返还。普通摄入物只能执行系统性安全操作，不能执行局部手术。
 */
public interface ConsumableEffectService {

    /** 物品成功摄入后应用其配置效果。 */
    void applyConsumable(ServerPlayer player, ItemStack stack);
}
