package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class SessionTimerModule extends HudModule {
	private long start = 0;

	public SessionTimerModule() {
		super("SessionTimer", "Время с момента входа в мир");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) {
			start = 0;
			setSize(60, 12);
			return;
		}
		if (start == 0) start = System.currentTimeMillis();
		long s = (System.currentTimeMillis() - start) / 1000;
		String text = String.format("Сессия: %02d:%02d", s / 60, s % 60);
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
