package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ServerData;

public class PingModule extends HudModule {
	public PingModule() {
		super("Ping", "Задержка до сервера");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		int ping = -1;
		if (mc.getConnection() != null && mc.player != null) {
			for (var info : mc.getConnection().getOnlinePlayers()) {
				if (info.getProfile().id().equals(mc.player.getUUID())) {
					ping = info.getLatency();
					break;
				}
			}
		}
		String text = "Ping: " + (ping >= 0 ? ping + " ms" : "—");
		setSize(mc.font.width(text) + 8, 12);
		int color = ping < 0 ? 0xFF9E9E9E : ping < 80 ? 0xFF4CAF50 : ping < 180 ? 0xFFFFC107 : 0xFFF44336;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
