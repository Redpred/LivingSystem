package com.redpred.livingsystem.client;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.client.screen.MedicalStationScreen;
import com.redpred.livingsystem.content.menu.MedicalMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 客户端菜单界面注册（模组事件总线，仅客户端）。把工作站菜单类型绑定到其容器界面。
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID, value = Dist.CLIENT)
public final class ClientMenuScreens {

    private ClientMenuScreens() {
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MedicalMenus.MEDICAL_STATION.get(), MedicalStationScreen::new);
    }
}
