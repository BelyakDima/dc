package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import net.minecraft.client.player.RemotePlayer;

import java.util.Locale;

public class PlayerProximityAlertModule extends Module {
	private final NumberSetting distance;
	private long lastAlert = 0;

	public PlayerProximityAlertModule() {
		super("PlayerProximityAlert", "Предупреждает, когда игрок подходит близко", Category.UTILITY);
		distance = new NumberSetting("Дистанция", "Блоки до игрока", 12, 3, 32, 1);
		register(distance);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null) return;
		if (System.currentTimeMillis() - lastAlert < 15000) return;
		RemotePlayer nearest = null;
		double best = Double.MAX_VALUE;
		for (var e : mc.level.entitiesForRendering()) {
			if (!(e instanceof RemotePlayer rp) || e == mc.player || !e.isAlive()) continue;
			double d = e.distanceTo(mc.player);
			if (d < best) {
				best = d;
				nearest = rp;
			}
		}
		if (nearest != null && best <= distance.get()) {
			lastAlert = System.currentTimeMillis();
			DarknessClient.getNotifications().show(
				String.format(Locale.ROOT, "§eРядом игрок: §f%s §7(%.1fм)", nearest.getName().getString(), best));
		}
	}
}
