package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

public class EspModule extends Module {
	private final BooleanSetting playersOnly;
	private final ColorSetting color;
	private final ColorSetting hitColor;

	public EspModule() {
		super("ESP", "2D-рамки вокруг сущностей", Category.VISUALS);
		playersOnly = new BooleanSetting("Только игроки", "Рамки только вокруг игроков", true);
		color = new ColorSetting("Цвет", "Цвет рамки", 0xFF7C4DFF);
		hitColor = new ColorSetting("Цвет цели", "Цвет рамки наведённой цели", 0xFFFF5252);
		register(playersOnly, color, hitColor);
	}

	public void renderOverlay(GuiGraphics g) {
		if (mc.level == null || mc.player == null) return;
		for (Entity e : mc.level.entitiesForRendering()) {
			if (e == mc.player || !e.isAlive()) continue;
			boolean isPlayer = e instanceof RemotePlayer;
			if (playersOnly.get() && !isPlayer) continue;
			if (e.distanceTo(mc.player) > 96) continue;

			double minX = e.getX() - e.getBbWidth() / 2, maxX = e.getX() + e.getBbWidth() / 2;
			double minY = e.getY(), maxY = e.getY() + e.getBbHeight();
			double minZ = e.getZ() - e.getBbWidth() / 2, maxZ = e.getZ() + e.getBbWidth() / 2;

			float[][] corners = new float[8][];
			int i = 0;
			boolean ok = true;
			for (double y : new double[]{minY, maxY}) {
				for (double x : new double[]{minX, maxX}) {
					for (double z : new double[]{minZ, maxZ}) {
						float[] p = RenderUtil.worldToScreen(x, y, z);
						if (p == null) {
							ok = false;
							break;
						}
						corners[i++] = p;
					}
				}
			}
			if (!ok || i < 8) continue;

			float left = Float.MAX_VALUE, right = -Float.MAX_VALUE, top = Float.MAX_VALUE, bottom = -Float.MAX_VALUE;
			for (float[] c : corners) {
				left = Math.min(left, c[0]);
				right = Math.max(right, c[0]);
				top = Math.min(top, c[1]);
				bottom = Math.max(bottom, c[1]);
			}
			boolean hovered = mc.crosshairPickEntity == e;
			int c = hovered ? hitColor.rgb() : color.rgb();
			RenderUtil.outline(g, (int) left, (int) top, (int) (right - left), (int) (bottom - top), RenderUtil.withAlpha(c, 220));
			RenderUtil.rect(g, (int) left, (int) top, 2, 2, c);
			RenderUtil.rect(g, (int) right - 2, (int) top, 2, 2, c);
			RenderUtil.rect(g, (int) left, (int) bottom - 2, 2, 2, c);
			RenderUtil.rect(g, (int) right - 2, (int) bottom - 2, 2, 2, c);

			if (isPlayer && mc.options.getCameraType().isFirstPerson()) {
				String name = e.getName().getString();
				RenderUtil.textCentered(g, name, (int) ((left + right) / 2), (int) top - 10, 0xFFFFFFFF);
			}
		}
	}
}
