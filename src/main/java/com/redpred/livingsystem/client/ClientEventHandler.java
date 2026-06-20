package com.redpred.livingsystem.client;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.client.key.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * 客户端游戏总线事件：在客户端 tick 中消费按键点击，打开健康界面。
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID, value = Dist.CLIENT)
public final class ClientEventHandler {

    private ClientEventHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        while (ModKeyMappings.OPEN_HEALTH.consumeClick()) {
            ClientHooks.openHealthScreen();
        }
    }
}
