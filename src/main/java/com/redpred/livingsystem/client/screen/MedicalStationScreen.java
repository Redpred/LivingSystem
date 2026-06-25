package com.redpred.livingsystem.client.screen;

import com.redpred.livingsystem.content.menu.MedicalStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * 医疗工作站容器界面（见开发文档 §16.2）。无专用贴图时以纯色面板绘制背景与槽位底框，
 * 保证功能可用；美术贴图在阶段七补充。
 */
public final class MedicalStationScreen extends AbstractContainerScreen<MedicalStationMenu> {

    public MedicalStationScreen(MedicalStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 150;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        // 面板背景与边框。
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFFC6C6C6);
        guiGraphics.fill(x + 2, y + 2, x + this.imageWidth - 2, y + this.imageHeight - 2, 0xFF8B8B8B);
        // 工作站 9 格底框。
        for (int col = 0; col < 9; col++) {
            int sx = x + 8 + col * 18;
            guiGraphics.fill(sx, y + 36, sx + 16, y + 36 + 16, 0xFF373737);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
