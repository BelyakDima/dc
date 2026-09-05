package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.StringSetting;

public class AutoWelcomeModule extends Module {
	private final StringSetting message;
	private boolean wasInWorld = false;
	private long joinTime = 0;
	private boolean sent = false;

	public AutoWelcomeModule() {
		super("AutoWelcome", "Приветственное сообщение в чат при входе в мир", Category.UTILITY);
		message = new StringSetting("Сообщение", "Текст приветствия", "Всем привет!");
		register(message);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null) {
			wasInWorld = false;
			return;
		}
		if (!wasInWorld) {
			wasInWorld = true;
			joinTime = System.currentTimeMillis();
			sent = false;
		}
		if (!sent && System.currentTimeMillis() - joinTime > 3000) {
			sent = true;
			if (mc.getConnection() != null && !message.get().isBlank()) {
				mc.getConnection().sendChat(message.get());
			}
		}
	}
}
