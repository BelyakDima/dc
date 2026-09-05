package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class KillCounterModule extends HudModule {
	public KillCounterModule() {
		super("KillCounter", "Убийства за сессию");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		int kills = DarknessClient.getTargetManager().sessionKills;
		String text = "Убийств: " + kills;
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), kills > 0 ? 0xFF66BB6A : 0xFF9E9E9E);
	}
}
