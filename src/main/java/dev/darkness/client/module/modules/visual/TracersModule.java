package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.ModeSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

public class TracersModule extends Module implements dev.darkness.client.module.OverlayRenderer {
	private final ModeSetting targets;
	private final ColorSetting color;
	private final ColorSetting friendColor;

	public TracersModule() {
		super("Tracers", "Линии к игрокам и мобам", Category.VISUALS);
		targets = new ModeSetting("Цели", "К кому вести линии", java.util.List.of("Игроки", "Игроки+мобы"), "Игроки");
		color = new ColorSetting("Цвет", "Цвет линий", 0xFF7C4DFF);
		friendColor = new ColorSetting("Цвет цели", "Цвет линии до наведённой цели", 0xFFFF5252);
		register(targets, color, friendColor);
	}

	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null || mc.player == null) return;
		float sx = screenW / 2f, sy = screenH * 0.52f;
		for (Entity e : mc.level.entitiesForRendering()) {
			if (e == mc.player || !e.isAlive()) continue;
			boolean isPlayer = e instanceof RemotePlayer;
			if (!isPlayer && !targets.is("Игроки+мобы")) continue;
			if (isPlayer && e.distanceTo(mc.player) > 128) continue;
			if (!isPlayer && e.distanceTo(mc.player) > 64) continue;

			float[] pos = RenderUtil.worldToScreen(e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ());
			if (pos == null) continue;
			boolean hovered = mc.crosshairPickEntity == e;
			int c = hovered ? friendColor.rgb() : color.rgb();
			RenderUtil.line(g, sx, sy, pos[0], pos[1], RenderUtil.withAlpha(c, 200));
		}
	}
}
