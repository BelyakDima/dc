package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class MoonPhaseHudModule extends HudModule {
	private static final String[] PHASES = {
		"Полнолуние", "Убывающая", "Последняя четверть", "Убывающий серп",
		"Новолуние", "Растущий серп", "Первая четверть", "Растущая"
	};

	public MoonPhaseHudModule() {
		super("MoonPhaseHUD", "Текущая фаза луны");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null) {
			setSize(70, 12);
			return;
		}
		int phase = (int) ((mc.level.getDayTime() / 24000L % 8L + 8L) % 8L);
		String text = "Луна: " + PHASES[phase];
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
