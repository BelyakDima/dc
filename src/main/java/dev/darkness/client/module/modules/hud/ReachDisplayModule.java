package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class ReachDisplayModule extends HudModule {
	public ReachDisplayModule() {
		super("ReachDisplay", "Дистанция последнего удара");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		double reach = DarknessClient.getTargetManager().getLastReach();
		String text = String.format("Reach: %.2f", reach);
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
