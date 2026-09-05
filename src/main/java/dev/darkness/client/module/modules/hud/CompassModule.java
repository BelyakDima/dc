package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class CompassModule extends HudModule {
	private static final String[] DIRS = {"Ю", "ЮЗ", "З", "СЗ", "С", "СВ", "В", "ЮВ"};

	public CompassModule() {
		super("Compass", "Компас-полоса с направлениями");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		int w = 150, h = 12;
		setSize(w, h);
		int cx = getX(screenW) + w / 2;
		int y = getY(screenH);
		RenderUtil.rect(g, getX(screenW), y, w, h, 0x900D0D14);
		float yaw = mc.player.getYRot();
		for (int i = 0; i < 8; i++) {
			float dirAngle = i * 45f;
			float rel = dirAngle - yaw;
			while (rel > 180) rel -= 360;
			while (rel < -180) rel += 360;
			if (Math.abs(rel) > 75) continue;
			int x = cx + (int) (rel / 75f * (w / 2 - 12));
			boolean cardinal = i % 2 == 0;
			int alpha = 255 - (int) (160 * Math.abs(rel) / 75f);
			int color = RenderUtil.withAlpha(cardinal ? 0xFFFFFFFF : 0xFFB0B0B0, alpha);
			RenderUtil.textCentered(g, DIRS[i], x, y + 2, cardinal ? color : RenderUtil.withAlpha(0xFFB0B0B0, alpha));
		}
		// центральная метка
		RenderUtil.rect(g, cx, y, 1, h, 0xFFFF5252);
	}
}
