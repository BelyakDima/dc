package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.module.modules.util.WaypointModule;
import dev.darkness.client.util.RenderUtil;
import dev.darkness.client.util.RoundedUI;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Locale;

/** Пузырёк-метка как у PulseVisuals: иконка дома + имя + метраж. */
public class WaypointHudModule extends HudModule {
	public WaypointHudModule() {
		super("WaypointHUD", "Пузырёк ближайшей точки (имя + метраж)");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		var wps = WaypointModule.getWaypoints();
		if (mc.player == null || wps.isEmpty()) {
			setSize(90, 30);
			return;
		}
		WaypointModule.Waypoint best = null;
		double bestDist = Double.MAX_VALUE;
		for (var wp : wps) {
			double d = mc.player.distanceToSqr(wp.x, wp.y, wp.z);
			if (d < bestDist) {
				bestDist = d;
				best = wp;
			}
		}
		if (best == null) return;
		double dist = Math.sqrt(bestDist);
		String meters = String.format(Locale.ROOT, "%.0f метров", dist);
		int w = Math.max(60, mc.font.width(meters) + 38);
		int h = 28;
		setSize(w, h + 6);
		int x = getX(screenW), y = getY(screenH);
		RoundedUI.panel(g, x, y, w, h);
		// иконка-дом (фиолетовый квадрат с вырезом)
		RenderUtil.rect(g, x + 4, y + 6, 12, 10, 0xFF7C4DFF);
		RenderUtil.rect(g, x + 8, y + 12, 4, 4, 0xFF111118);
		RenderUtil.text(g, best.name, x + 20, y + 4, 0xFFF0F0F0);
		RenderUtil.text(g, meters, x + 20, y + 13, 0xFFB0B0B0);
		// хвостик-указатель вниз
		RenderUtil.rect(g, x + w / 2 - 1, y + h, 2, 3, RoundedUI.BG);
		RenderUtil.rect(g, x + w / 2 - 2, y + h + 2, 4, 2, RoundedUI.BG);
	}
}
