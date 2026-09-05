package dev.darkness.client.module.modules.visual;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class HitMarkerModule extends Module implements dev.darkness.client.module.OverlayRenderer {
	private final ColorSetting color;

	public HitMarkerModule() {
		super("HitMarker", "Крестик на прицеле при попадании по цели", Category.VISUALS);
		color = new ColorSetting("Цвет", "Цвет крестика", 0xFFFFFFFF);
		register(color);
	}

	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		long age = System.currentTimeMillis() - DarknessClient.getTargetManager().lastHitMs;
		if (age < 0 || age > 350) return;
		float t = 1f - age / 350f;
		int cx = screenW / 2, cy = screenH / 2;
		int gap = 4 + (int) (4 * (1 - t));
		int len = 5;
		int argb = RenderUtil.withAlpha(color.rgb(), (int) (255 * t));
		RenderUtil.line(g, cx - gap - len, cy - gap - len, cx - gap, cy - gap, argb);
		RenderUtil.line(g, cx + gap, cy + gap, cx + gap + len, cy + gap + len, argb);
		RenderUtil.line(g, cx + gap, cy - gap, cx + gap + len, cy - gap - len, argb);
		RenderUtil.line(g, cx - gap - len, cy + gap + len, cx - gap, cy + gap, argb);
	}
}
