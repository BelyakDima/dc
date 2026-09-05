package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class ComboModule extends HudModule {
	public ComboModule() {
		super("ComboCounter", "Счётчик серии ударов");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		int combo = DarknessClient.getTargetManager().getCombo();
		String text = "Комбо: " + combo;
		setSize(mc.font.width(text) + 8, 12);
		int color = combo == 0 ? 0xFF9E9E9E : combo < 5 ? 0xFFE0E0E0 : 0xFFFF5252;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
