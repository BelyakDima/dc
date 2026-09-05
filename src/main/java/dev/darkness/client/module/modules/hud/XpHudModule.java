package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class XpHudModule extends HudModule {
	public XpHudModule() {
		super("XPHUD", "Точный уровень и прогресс опыта");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		String text = "XP: " + mc.player.experienceLevel
			+ String.format(" (%.0f%%)", mc.player.experienceProgress * 100);
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFF7CFC00);
	}
}
