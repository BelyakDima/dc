package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.module.modules.util.WaypointModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Locale;

public class WaypointHudModule extends HudModule {
	public WaypointHudModule() {
		super("WaypointHUD", "Ближайшая точка: имя и дистанция");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		var wps = WaypointModule.getWaypoints();
		if (mc.player == null || wps.isEmpty()) {
			setSize(80, 12);
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
		if (best == null) {
			setSize(80, 12);
			return;
		}
		double dist = Math.sqrt(bestDist);
		String text = String.format(Locale.ROOT, "%s §7(%.0fм)", best.name, dist);
		setSize(mc.font.width(text.replaceAll("§.", "")) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFB388FF);
	}
}
