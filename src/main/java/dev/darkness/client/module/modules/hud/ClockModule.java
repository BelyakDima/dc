package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockModule extends HudModule {
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

	public ClockModule() {
		super("Clock", "Реальное время на экране");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		String text = LocalTime.now().format(FMT);
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
