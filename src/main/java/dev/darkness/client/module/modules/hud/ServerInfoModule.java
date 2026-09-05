package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ServerData;

public class ServerInfoModule extends HudModule {
	public ServerInfoModule() {
		super("ServerInfo", "Адрес текущего сервера");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		ServerData server = mc.getCurrentServer();
		String text = "Сервер: " + (server != null ? server.ip : "одиночная игра");
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
