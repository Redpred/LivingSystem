package com.redpred.livingsystem.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

/**
 * LivingSystem 健康 HUD 图层（见开发文档 §15.3）。
 *
 * <p>阶段一为空壳，不绘制任何内容。后续阶段在此绘制：身体部位状态、血液容量、体力、水分、呼吸储备
 * 与状态图标列表，均读取 {@code ClientHealthState} 的只读快照，并受客户端配置控制。</p>
 */
public final class LivingHealthHudLayer implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // 阶段一占位：暂不绘制
    }
}
