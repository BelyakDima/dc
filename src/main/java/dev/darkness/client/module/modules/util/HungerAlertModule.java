package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;

public class HungerAlertModule extends Module {
	private final NumberSetting threshold;
	private long lastAlert = 0;

	public HungerAlertModule() {
		super("HungerAlert", "Предупреждает при голоде ниже порога (регенерация стопорится на 6)", Category.UTILITY);
		threshold = new NumberSetting("Порог", "Уровень голода для предупреждения", 8, 1, 19, 1);
		register(threshold);
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		long now = System.currentTimeMillis();
		if (now - lastAlert < 30_000) return;
		if (mc.player.getFoodData().getFoodLevel() <= (int) Math.round(threshold.get())) {
			lastAlert = now;
			DarknessClient.getNotifications().show("§eГолод low! §fПоешьте, регенерация остановится");
		}
	}
}
