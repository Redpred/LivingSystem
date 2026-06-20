package com.redpred.livingsystem.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 健康系统主界面（见开发文档 §15.2）。
 *
 * <p>阶段一为空壳：仅渲染标题与占位说明，可正常打开/关闭。完整页签（概览/伤势/生命体征/暴露与疾病/
 * 治疗与药物/防护装备/恢复状态）在后续阶段实现，并按信息等级过滤显示客户端只读快照。</p>
 */
public final class HealthScreen extends Screen {

    public HealthScreen() {
        super(Component.translatable("screen.livingsystem.health"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("screen.livingsystem.health.placeholder"),
                this.width / 2, 60, 0xA0A0A0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
