package com.redpred.livingsystem.client.hud;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

/**
 * 注册 LivingSystem 的 HUD 图层（见开发文档 §15.3）。阶段一注册一个空壳图层。
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID, value = Dist.CLIENT)
public final class LivingHudLayers {

    private LivingHudLayers() {
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "health_hud"),
                new LivingHealthHudLayer());
    }
}
