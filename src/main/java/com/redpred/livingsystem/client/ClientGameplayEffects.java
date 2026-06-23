package com.redpred.livingsystem.client;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.client.config.ClientConfig;
import com.redpred.livingsystem.client.state.ClientHealthState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * 客户端游戏性表现（见开发文档 §15.3、§15.4.2）：低灌注/重伤时的镜头轻微摇晃。
 *
 * <p>只读取 {@link ClientHealthState} 同步来的强度并做本地表现，不改变任何权威状态；受
 * {@link ClientConfig#ENABLE_CAMERA_SWAY} 控制。心跳音效将在注册自定义 {@code ModSounds} 后接入。</p>
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID, value = Dist.CLIENT)
public final class ClientGameplayEffects {

    private ClientGameplayEffects() {
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (!ClientConfig.ENABLE_CAMERA_SWAY.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        float sway = ClientHealthState.gameplaySway();
        if (sway <= 0.01F) {
            return;
        }
        float time = minecraft.player.tickCount + (float) event.getPartialTick();
        event.setRoll(event.getRoll() + Mth.sin(time * 0.3F) * sway * 2.0F);
    }
}
