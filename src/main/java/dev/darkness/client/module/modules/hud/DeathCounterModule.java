package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class DeathCounterModule extends HudModule {
	public DeathCounterModule() {
		super("DeathCounter", "Смерти за сессию");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		int deaths = DarknessClient.getTargetManager().sessionDeaths;
		String text = "Смертей: " + deaths;
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), deaths > 0 ? 0xFFFF5252 : 0xFF9E9E9E);
	}
}
