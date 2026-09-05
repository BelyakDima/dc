package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;

public class DropProtectModule extends Module {
	public DropProtectModule() {
		super("DropProtect", "Защита от случайного выброса вещи клавишей Q", Category.UTILITY);
	}

	@Override
	public void onTick() {
		// поглощаем все «клики» клавиши выброса до того, как их увидит ванилла
		while (mc.options.keyDrop.consumeClick()) {
		}
	}
}
