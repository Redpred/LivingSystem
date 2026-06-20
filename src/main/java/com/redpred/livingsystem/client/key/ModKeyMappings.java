package com.redpred.livingsystem.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * 客户端按键（见开发文档 §15.2.1）。H 默认打开健康界面，其余按键默认不绑定以避免与整合包冲突。
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID, value = Dist.CLIENT)
public final class ModKeyMappings {

    /** 按键分类（翻译键）。 */
    public static final String CATEGORY = "key.categories.livingsystem";

    /** 打开健康界面，默认 H。 */
    public static final KeyMapping OPEN_HEALTH = new KeyMapping(
            "key.livingsystem.open_health",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY);

    private ModKeyMappings() {
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_HEALTH);
    }
}
