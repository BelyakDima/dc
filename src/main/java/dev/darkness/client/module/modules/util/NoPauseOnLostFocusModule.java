package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;

public class NoPauseOnLostFocusModule extends Module {
	private boolean previous = true;

	public NoPauseOnLostFocusModule() {
		super("NoPauseOnTab", "Игра не ставится на паузу при переключении окна", Category.UTILITY);
	}

	@Override
	protected void onEnable() {
		previous = mc.options.pauseOnLostFocus;
		mc.options.pauseOnLostFocus = false;
	}

	@Override
	protected void onDisable() {
		mc.options.pauseOnLostFocus = previous;
	}
}
