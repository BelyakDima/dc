package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class WeatherHudModule extends HudModule {
	public WeatherHudModule() {
		super("WeatherHUD", "Текущая погода в мире");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null) {
			setSize(50, 12);
			return;
		}
		String text;
		int color;
		if (mc.level.isThundering()) {
			text = "Гроза";
			color = 0xFFFFB300;
		} else if (mc.level.isRaining()) {
			text = "Дождь";
			color = 0xFF4FC3F7;
		} else {
			text = "Ясно";
			color = 0xFFB0B0B0;
		}
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
