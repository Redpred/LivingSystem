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

        renderTreatmentProgress(guiGraphics, minecraft);
    }

    /** 治疗进行中时在屏幕下方居中绘制进度条。 */
    private void renderTreatmentProgress(GuiGraphics guiGraphics, Minecraft minecraft) {
        var progress = ClientHealthState.getTreatmentProgress();
        if (progress == null || progress.interrupted() || progress.progress() >= 1.0F) {
            return;
        }
        int width = guiGraphics.guiWidth();
        int barWidth = 100;
        int x = (width - barWidth) / 2;
        int y = guiGraphics.guiHeight() - 50;
        int filled = (int) (barWidth * Math.max(0.0F, Math.min(1.0F, progress.progress())));
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + 5, 0xFF000000);
        guiGraphics.fill(x, y, x + filled, y + 4, 0xFF33CC44);
        String label = String.format("治疗中 %d%%", (int) (progress.progress() * 100));
        guiGraphics.drawCenteredString(minecraft.font, label, width / 2, y - 10, 0xFFFFFFFF);
    }
}
