package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class FpsModule extends HudModule {
	public FpsModule() {
		super("FPS", "Счётчик кадров");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		String text = "FPS: " + mc.getFps();
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
