package com.redpred.livingsystem.client.hud;

import com.redpred.livingsystem.client.config.ClientConfig;
import com.redpred.livingsystem.client.state.ClientHealthState;
import com.redpred.livingsystem.network.payload.HudSummaryPayload;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

/**
 * LivingSystem 健康 HUD 图层（见开发文档 §15.3）。
 *
 * <p>阶段二：以文字显示血液、体力、水分、呼吸的 0~20 展示值（读取 {@link ClientHealthState} 的只读快照）；
 * 完整美术化 HUD（部位状态、状态图标）在后续阶段。</p>
 */
public final class LivingHealthHudLayer implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        if (!ClientConfig.SHOW_HEALTH_HUD.get()) {
            return;
        }
        HudSummaryPayload hud = ClientHealthState.getHud();
        if (hud == null) {
            return;
        }
        String text = String.format("血液 %.0f/20   体力 %.0f/20   水分 %.0f/20   呼吸 %.0f/20",
                hud.blood(), hud.stamina(), hud.hydration(), hud.respiratory());
        guiGraphics.drawString(minecraft.font, text, 8, guiGraphics.guiHeight() - 20, 0xFFFFFFFF);
    }
}
