package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;

public class ToggleSprintModule extends Module {
	public ToggleSprintModule() {
		super("ToggleSprint", "Автоспринт: бежит всегда при движении вперёд", Category.UTILITY);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.screen != null) return;
		if (mc.options.keyUp.isDown() && !mc.player.isSprinting()) {
			mc.player.setSprinting(true);
		}
	}
}
