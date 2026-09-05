package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HitBoxesModule extends Module implements OverlayRenderer {
	private final ColorSetting color;
	private final BooleanSetting particles;
	private final NumberSetting particleDensity;
	private final NumberSetting particleSpeed;

	public HitBoxesModule() {
		super("HitBoxes", "Каркасные хитбоксы сущностей (только в прямой видимости)", Category.VISUALS);
		color = new ColorSetting("Цвет", "Цвет каркаса", 0xFF00E5FF);
		particles = new BooleanSetting("Частицы", "Бегущие точки по рёбрам хитбокса", true);
		particleDensity = new NumberSetting("Плотность частиц", "Точек на блок ребра", 2, 1, 8, 1);
		particleSpeed = new NumberSetting("Скорость частиц", "Скорость бега точек", 1, 0.25, 3, 0.25);
		register(color, particles, particleDensity, particleSpeed);
	}

	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null || mc.player == null) return;
		long now = System.currentTimeMillis();
		for (Entity e : mc.level.entitiesForRendering()) {
			if (e == mc.player || !(e instanceof LivingEntity) || !e.isAlive()) continue;
			if (e.distanceTo(mc.player) > 64) continue;
			// только в прямой видимости — никаких сквозных стен
			if (!mc.player.hasLineOfSight(e)) continue;

			double hw = e.getBbWidth() / 2, h = e.getBbHeight();
			float[][] c = new float[8][];
			int i = 0;
			boolean ok = true;
			for (double yy : new double[]{e.getY(), e.getY() + h}) {
				for (double xx : new double[]{e.getX() - hw, e.getX() + hw}) {
					for (double zz : new double[]{e.getZ() - hw, e.getZ() + hw}) {
						float[] s = RenderUtil.worldToScreen(xx, yy, zz);
						if (s == null) { ok = false; break; }
						c[i++] = s;
					}
				}
			}
			if (!ok) continue;
			int argb = RenderUtil.withAlpha(color.rgb(), 200);
			int[][] edges = {{0,1},{1,3},{3,2},{2,0},{4,5},{5,7},{7,6},{6,4},{0,4},{1,5},{3,7},{2,6}};
			for (int[] ed : edges) {
				float[] a = c[ed[0]], b = c[ed[1]];
				RenderUtil.line(g, a[0], a[1], b[0], b[1], argb);
			}
			// частицы: бегущие светящиеся точки по рёбрам
			if (particles.get()) {
				int pc = particleColor();
				double speed = particleSpeed.get();
				int density = (int) Math.round(particleDensity.get());
				float phase = (now % 2000L) / 2000f * (float) speed;
				for (int[] ed : edges) {
					float[] a = c[ed[0]], b = c[ed[1]];
					double ex = b[0] - a[0], ey = b[1] - a[1];
					double len = Math.sqrt(ex * ex + ey * ey);
					if (len < 6) continue;
					int n = Math.max(1, (int) (len / 14) * density);
					for (int k = 0; k < n; k++) {
						double t = (phase + (double) k / n) % 1.0;
						int px = (int) (a[0] + ex * t);
						int py = (int) (a[1] + ey * t);
						float fade = 0.55f + 0.45f * (float) Math.sin(t * Math.PI);
						RenderUtil.rect(g, px - 1, py - 1, 2, 2, RenderUtil.withAlpha(pc, (int) (230 * fade)));
					}
				}
			}
		}
	}

	private int particleColor() {
		float[] hsb = java.awt.Color.RGBtoHSB(color.red(), color.green(), color.blue(), null);
		float bright = 0.75f + 0.25f * (float) Math.sin(System.currentTimeMillis() / 300.0);
		return java.awt.Color.HSBtoRGB(hsb[0], Math.min(1f, hsb[1] + 0.2f), bright);
	}
}
