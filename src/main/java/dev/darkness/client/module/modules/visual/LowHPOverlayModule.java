package dev.darkness.client.module.modules.visual;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class LowHPOverlayModule extends Module implements OverlayRenderer {
	private final ColorSetting color;
	private final NumberSetting threshold;

	public LowHPOverlayModule() {
		super("LowHPOverlay", "Красная пульсирующая рамка при низком здоровье", Category.VISUALS);
		color = new ColorSetting("Цвет", "Цвет рамки", 0xFFFF1744);
		threshold = new NumberSetting("Порог, %", "Здоровье для показа", 35, 5, 80, 5);
		register(color, threshold);
	}

	@Override
	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		float t = mc.player.getHealth() / mc.player.getMaxHealth();
		float pct = threshold.get().floatValue() / 100f;
		if (t > pct) return;
		float intensity = (1 - t / pct);
		float pulse = 0.55f + 0.45f * (float) Math.sin(System.currentTimeMillis() / 180.0);
		int alpha = (int) (170 * intensity * pulse);
		if (alpha <= 4) return;
		int argb = RenderUtil.withAlpha(color.rgb(), alpha);
		int th = 5 + (int) (6 * intensity);
		RenderUtil.rect(g, 0, 0, screenW, th, argb);
		RenderUtil.rect(g, 0, screenH - th, screenW, th, argb);
		RenderUtil.rect(g, 0, 0, th, screenH, argb);
		RenderUtil.rect(g, screenW - th, 0, th, screenH, argb);
	}
}
