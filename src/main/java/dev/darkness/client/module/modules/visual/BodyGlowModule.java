package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/** Body Glow — мягкое свечение вокруг игроков. */
public class BodyGlowModule extends Module implements OverlayRenderer {
	private final ColorSetting color;
	private final NumberSetting range;
	private final BooleanSetting pulse;

	public BodyGlowModule() {
		super("Body Glow", "Мягкое свечение вокруг игроков", Category.VISUALS);
		color = new ColorSetting("Цвет", "Цвет свечения", 0xFFFF6A3D);
		range = new NumberSetting("Дистанция", "Блоки", 24, 4, 64, 2);
		pulse = new BooleanSetting("Пульсация", "Свечение дышит", true);
		register(color, range, pulse);
	}

	@Override
	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null || mc.player == null) return;
		long now = System.currentTimeMillis();
		float pulseF = pulse.get() ? 0.7f + 0.3f * (float) Math.sin(now * 0.004) : 1f;

		for (Entity e : mc.level.entitiesForRendering()) {
			if (!(e instanceof RemotePlayer rp) || e == mc.player || !e.isAlive()) continue;
			if (e.distanceTo(mc.player) > range.get()) continue;
			float[] c = RenderUtil.worldToScreen(e.getX(), e.getY() + e.getBbHeight() * 0.45, e.getZ());
			if (c == null) continue;
			// радиус свечения пропорционален проекции роста игрока
			float[] top = RenderUtil.worldToScreen(e.getX(), e.getY() + e.getBbHeight(), e.getZ());
			float[] bottom = RenderUtil.worldToScreen(e.getX(), e.getY(), e.getZ());
			if (top == null || bottom == null) continue;
			float ry = Math.abs(bottom[1] - top[1]) * 0.75f;
			float rx = ry * 1.5f;
			int argb = RenderUtil.withAlpha(color.rgb(), (int) (110 * pulseF));
			int layers = 3;
			for (int i = layers; i >= 1; i--) {
				float k = 1 + i * 0.28f;
				int la = (int) (argb >>> 24) / (i * 2);
				drawEllipseOutline(g, (int) c[0], (int) c[1], (int) (rx * k), (int) (ry * k),
					RenderUtil.withAlpha(color.rgb(), la), 22);
			}
		}
	}

	private static void drawEllipseOutline(GuiGraphics g, int cx, int cy, int rx, int ry, int argb, int segs) {
		int px = cx + rx, py = cy;
		for (int i = 1; i <= segs; i++) {
			double ang = i / (double) segs * 2 * Math.PI;
			int nx = cx + (int) Math.round(Math.cos(ang) * rx);
			int ny = cy + (int) Math.round(Math.sin(ang) * ry);
			RenderUtil.line(g, px, py, nx, ny, argb);
			px = nx;
			py = ny;
		}
	}
}
