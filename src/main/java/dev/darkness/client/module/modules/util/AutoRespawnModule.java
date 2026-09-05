package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.client.gui.screens.DeathScreen;

public class AutoRespawnModule extends Module {
	public AutoRespawnModule() {
		super("AutoRespawn", "Мгновенное возрождение без экрана смерти", Category.UTILITY);
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		if (mc.screen instanceof DeathScreen && mc.player.isDeadOrDying()) {
			mc.player.respawn();
			mc.setScreen(null);
		}
	}
}
