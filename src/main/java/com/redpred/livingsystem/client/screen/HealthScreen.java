package com.redpred.livingsystem.client.screen;

import com.redpred.livingsystem.client.state.ClientHealthState;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload.InjuryEntry;
import com.redpred.livingsystem.network.payload.HealthScreenSnapshotPayload.VitalsSummary;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 健康系统主界面（见开发文档 §15.2）。
 *
 * <p>展示服务端只读快照（{@link ClientHealthState#getHealthScreen()}）：左侧七部位健康度小人，
 * 右侧全身体征摘要，下方当前伤势列表。属自查可观察级别信息；精确设备数值由医疗检查提供。</p>
 */
public final class HealthScreen extends Screen {

    private static final String[] REGION_NAMES = {"头部", "胸腔", "腹部", "左臂", "右臂", "左腿", "右腿"};

    public HealthScreen() {
        super(Component.translatable("screen.livingsystem.health"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 16, 0xFFFFFF);

        HealthScreenSnapshotPayload snap = ClientHealthState.getHealthScreen();
        if (snap == null) {
            g.drawCenteredString(this.font, Component.literal("等待服务端健康数据…"), this.width / 2, 40, 0xA0A0A0);
            return;
        }
        renderRegions(g, snap.regionSeverities());
        renderVitals(g, snap.vitals());
        renderInjuries(g, snap.injuries());
    }

    /** 左列：七部位健康度（绿→黄→红）。 */
    private void renderRegions(GuiGraphics g, List<Float> severities) {
        int x = this.width / 2 - 150;
        int y = 44;
        g.drawString(this.font, "身体部位", x, y, 0xFFFFAA00);
        for (int i = 0; i < severities.size() && i < REGION_NAMES.length; i++) {
            float sev = severities.get(i);
            int color = healthColor(1.0F - sev);
            String line = String.format("%s  %d%%", REGION_NAMES[i], (int) ((1.0F - sev) * 100));
            g.drawString(this.font, line, x, y + 14 + i * 12, color);
        }
    }

    /** 右列：全身体征摘要。 */
    private void renderVitals(GuiGraphics g, VitalsSummary v) {
        int x = this.width / 2 + 20;
        int y = 44;
        g.drawString(this.font, "生命体征", x, y, 0xFFFFAA00);
        g.drawString(this.font, String.format("血容量 %d%%", pct(v.bloodFraction())), x, y + 14, 0xFFE05050);
        g.drawString(this.font, String.format("体力 %d%%", pct(v.stamina())), x, y + 26, 0xFF50C0E0);
        g.drawString(this.font, String.format("水分 %d%%", pct(v.hydration())), x, y + 38, 0xFF5080E0);
        g.drawString(this.font, String.format("呼吸 %d%%", pct(v.respiratory())), x, y + 50, 0xFF80E0C0);
        g.drawString(this.font, String.format("疼痛 %d%%", pct(v.totalPain())), x, y + 62, 0xFFE0A050);
        g.drawString(this.font, String.format("意识 %d%%", pct(v.consciousness())), x, y + 74, 0xFFC0C0FF);
    }

    /** 下方：当前伤势列表。 */
    private void renderInjuries(GuiGraphics g, List<InjuryEntry> injuries) {
        int x = this.width / 2 - 150;
        int y = 150;
        g.drawString(this.font, "当前伤势", x, y, 0xFFFFAA00);
        if (injuries.isEmpty()) {
            g.drawString(this.font, "无", x, y + 14, 0xFF80E080);
            return;
        }
        int row = 0;
        for (InjuryEntry e : injuries) {
            if (row >= 8) {
                break;
            }
            String region = e.regionIndex() >= 0 && e.regionIndex() < REGION_NAMES.length
                    ? REGION_NAMES[e.regionIndex()] : "全身";
            StringBuilder sb = new StringBuilder();
            sb.append(region).append(" · ").append(e.kindLabel())
                    .append(" ").append((int) (e.severity() * 100)).append("%");
            if (e.bleeding()) {
                sb.append(" §c[出血]§r");
            }
            if (e.fractureGrade() > 0) {
                sb.append(" §e[骨折").append(e.fractureGrade()).append("]§r");
            }
            if (e.treated()) {
                sb.append(" §a[已处理]§r");
            }
            g.drawString(this.font, sb.toString(), x, y + 14 + row * 12, 0xFFCCCCCC);
            row++;
        }
    }

    private static int pct(float fraction) {
        return Math.round(Math.max(0.0F, Math.min(1.0F, fraction)) * 100.0F);
    }

    /** 健康度颜色：1.0 绿、0.5 黄、0.0 红。 */
    private static int healthColor(float health) {
        health = Math.max(0.0F, Math.min(1.0F, health));
        int r = (int) (255 * (1.0F - health));
        int gc = (int) (255 * health);
        return 0xFF000000 | (r << 16) | (gc << 8) | 0x20;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
