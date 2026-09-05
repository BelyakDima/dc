package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class PlayerCountModule extends HudModule {
	public PlayerCountModule() {
		super("PlayerCount", "Сколько игроков онлайн на сервере");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		int n = mc.getConnection() != null ? mc.getConnection().getOnlinePlayers().size() : -1;
		String text = "Игроков: " + (n >= 0 ? n : "—");
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
