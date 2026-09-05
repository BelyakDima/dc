package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class AirHudModule extends HudModule {
	public AirHudModule() {
		super("AirHUD", "Кислород под водой в процентах");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		int air = mc.player.getAirSupply();
		int max = mc.player.getMaxAirSupply();
		if (air >= max) {
			setSize(60, 12);
			return;
		}
		int pct = (int) (air * 100f / max);
		String text = "Кислород: " + pct + "%";
		setSize(mc.font.width(text) + 8, 12);
		int color = pct > 50 ? 0xFF4FC3F7 : pct > 25 ? 0xFFFFC107 : 0xFFF44336;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
