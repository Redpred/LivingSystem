package com.redpred.livingsystem.data;

import com.redpred.livingsystem.LivingSystemMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/**
 * 注册 LivingSystem 的数据包重载监听器（游戏总线 {@link AddReloadListenerEvent}）。
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID)
public final class ModReloadListeners {

    private ModReloadListeners() {
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new DamageProfileReloadListener());
        event.addListener(new TreatmentDefinitionReloadListener());
        event.addListener(new EnvironmentalHazardReloadListener());
        event.addListener(new ProtectionProfileReloadListener());
        event.addListener(new ToxinDefinitionReloadListener());
        event.addListener(new PathogenDefinitionReloadListener());
        event.addListener(new RadiationDefinitionReloadListener());
    }
}
