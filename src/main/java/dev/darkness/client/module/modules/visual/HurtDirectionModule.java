package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class HurtDirectionModule extends Module implements dev.darkness.client.module.OverlayRenderer {
	private final ColorSetting color;

	public HurtDirectionModule() {
		super("HurtDirection", "Маркер в сторону того, кто вас ударил", Category.VISUALS);
		color = new ColorSetting("Цвет", "Цвет маркера", 0xFFFF5252);
		register(color);
	}

	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		var tm = dev.darkness.client.DarknessClient.getTargetManager();
		long age = System.currentTimeMillis() - tm.selfHurtMs;
		if (age < 0 || age > 3000) return;
		float[] pos = RenderUtil.worldToScreen(tm.selfHurtX, tm.selfHurtY, tm.selfHurtZ);
		int cx = screenW / 2, cy = screenH / 2;
		int argb = RenderUtil.withAlpha(color.rgb(), (int) (255 * (1 - age / 3000f)));
		if (pos == null) {
			// атакующий за спиной — точка на краю экрана
			double ax = tm.selfHurtX - mc.player.getX();
			double az = tm.selfHurtZ - mc.player.getZ();
			float yaw = (float) (Math.toDegrees(Math.atan2(-ax, az)));
			float rel = yaw - mc.player.getYRot();
			while (rel > 180) rel -= 360;
			while (rel < -180) rel += 360;
			int ex = cx + (int) (Math.toRadians(rel) / Math.toRadians(90) * (screenW / 2 - 20));
			int ey = screenH / 2;
			ex = Math.max(20, Math.min(screenW - 20, ex));
			for (int dx = -3; dx <= 3; dx++) for (int dy = -3; dy <= 3; dy++)
				if (dx * dx + dy * dy <= 9) RenderUtil.rect(g, ex + dx, ey + dy, 1, 1, argb);
			return;
		}
		// виден — рисуем точку
		for (int dx = -3; dx <= 3; dx++) for (int dy = -3; dy <= 3; dy++)
			if (dx * dx + dy * dy <= 9) RenderUtil.rect(g, (int) pos[0] + dx, (int) pos[1] + dy, 1, 1, argb);
	}
}
