package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class DimensionAlertModule extends Module {
	private ResourceKey<Level> lastDimension;

	public DimensionAlertModule() {
		super("DimensionAlert", "Уведомление при смене измерения", Category.UTILITY);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null) {
			lastDimension = null;
			return;
		}
		ResourceKey<Level> dim = mc.level.dimension();
		if (lastDimension != null && dim != lastDimension) {
			String name = dim == Level.NETHER ? "§cАд" : dim == Level.END ? "§5Край" : "§aОбычный мир";
			DarknessClient.getNotifications().show("Вы вошли: " + name);
		}
		lastDimension = dim;
	}
}
