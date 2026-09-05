package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.util.ChatUtil;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

import java.util.Map;

public class AutoReconnectModule extends Module {
	private final NumberSetting delay;
	private String lastServerIp;
	private long reconnectAt = -1;
	private int attempts = 0;

	public AutoReconnectModule() {
		super("AutoReconnect", "Автоматический заход на сервер после кика", Category.UTILITY);
		delay = new NumberSetting("Задержка, сек", "Пауза перед повторным входом", 3, 1, 30, 1);
		register(delay);
	}

	public void onDisconnect() {
		if (!isEnabled() || mc == null) return;
		ServerData server = mc.getCurrentServer();
		if (server == null || server.ip == null || server.ip.isBlank()) return;
		if (mc.screen instanceof DisconnectedScreen) {
			lastServerIp = server.ip;
			reconnectAt = System.currentTimeMillis() + (long) (delay.get() * 1000);
			attempts++;
		}
	}

	@Override
	public void onTick() {
		if (reconnectAt <= 0 || lastServerIp == null) return;
		if (System.currentTimeMillis() < reconnectAt) return;
		if (!(mc.screen instanceof DisconnectedScreen)) {
			reconnectAt = -1;
			return;
		}
		reconnectAt = -1;
		ServerAddress address = ServerAddress.parseString(lastServerIp);
		ServerData data = new ServerData("Darkness reconnect", lastServerIp, ServerData.Type.OTHER);
		ConnectScreen.startConnecting(
			mc.screen,
			mc,
			address,
			data,
			false,
			new TransferState(Map.of(), Map.of(), false)
		);
		ChatUtil.message("Переподключение к " + lastServerIp + " (попытка " + attempts + ")");
	}

	@Override
	public String getDisplayInfo() {
		return attempts > 0 ? String.valueOf(attempts) : null;
	}
}
