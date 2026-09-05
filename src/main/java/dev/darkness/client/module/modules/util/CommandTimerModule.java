package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.StringSetting;

public class CommandTimerModule extends Module {
	private final StringSetting command;
	private final NumberSetting minutes;
	private long nextSend = 0;

	public CommandTimerModule() {
		super("CommandTimer", "Автоматически отправляет команду каждые N минут (например /kit)", Category.UTILITY);
		command = new StringSetting("Команда", "Команда с ведущим слэшем", "/kit");
		minutes = new NumberSetting("Интервал, мин", "Раз в сколько минут", 10, 1, 60, 1);
		register(command, minutes);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.getConnection() == null) return;
		long now = System.currentTimeMillis();
		if (nextSend == 0) {
			nextSend = now + (long) (minutes.get() * 60_000);
			return;
		}
		if (now >= nextSend) {
			String cmd = command.get().trim();
			if (!cmd.isEmpty()) {
				if (cmd.startsWith("/")) {
					mc.getConnection().sendCommand(cmd.substring(1));
				} else {
					mc.getConnection().sendChat(cmd);
				}
			}
			nextSend = now + (long) (minutes.get() * 60_000);
		}
	}
}
