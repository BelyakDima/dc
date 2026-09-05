package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class RamModule extends HudModule {
	public RamModule() {
		super("RAM", "Использование памяти");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20;
		long max = Runtime.getRuntime().maxMemory() >> 20;
		String text = "RAM: " + used + " / " + max + " MB";
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
