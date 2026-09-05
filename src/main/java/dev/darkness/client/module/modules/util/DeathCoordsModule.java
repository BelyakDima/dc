package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.ChatUtil;

import java.util.Locale;

public class DeathCoordsModule extends Module {
	private boolean wasDead = false;
	private double deathX, deathY, deathZ;

	public DeathCoordsModule() {
		super("DeathCoords", "Координаты смерти в чат после возрождения", Category.UTILITY);
	}

	@Override
	public void onTick() {
		if (mc.player == null) {
			wasDead = false;
			return;
		}
		boolean dead = mc.player.isDeadOrDying();
		if (dead && !wasDead) {
			deathX = mc.player.getX();
			deathY = mc.player.getY();
			deathZ = mc.player.getZ();
		}
		if (!dead && wasDead) {
			String coords = String.format(Locale.ROOT, "%.0f %.0f %.0f", deathX, deathY, deathZ);
			ChatUtil.message("§cМесто смерти: §f" + coords + " §8(в буфере)");
			if (mc.keyboardHandler != null) {
				mc.keyboardHandler.setClipboard(coords);
			}
		}
		wasDead = dead;
	}
}
