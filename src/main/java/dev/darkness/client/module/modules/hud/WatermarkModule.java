package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class WatermarkModule extends HudModule {
	public WatermarkModule() {
		super("Watermark", "Логотип клиента с версией и FPS");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		int fps = mc.getFps();
		String text = DarknessClient.MOD_NAME + " §7" + DarknessClient.VERSION + " §8| §f" + fps + " fps";
		String plain = DarknessClient.MOD_NAME + " " + DarknessClient.VERSION + " | " + fps + " fps";
		int w = mc.font.width(plain) + 12;
		int h = 16;
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		RenderUtil.rect(g, x, y, w, h, 0xC80D0D14);
		RenderUtil.rect(g, x, y, w, 1, 0xFF7C4DFF);
		RenderUtil.rect(g, x, y + h - 1, w, 1, 0xFF7C4DFF);
		RenderUtil.text(g, DarknessClient.MOD_NAME, x + 6, y + 4, 0xFFB388FF);
		int nameW = mc.font.width(DarknessClient.MOD_NAME);
		g.drawString(mc.font, " " + DarknessClient.VERSION + "  " + fps + " fps", x + 6 + nameW, y + 4, 0xFF9E9E9E, true);
	}
}
