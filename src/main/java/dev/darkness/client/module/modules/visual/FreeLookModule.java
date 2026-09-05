package dev.darkness.client.module.modules.visual;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.KeyManager;

public class FreeLookModule extends Module {
	public FreeLookModule() {
		super("FreeLook", "Свободный обзор (Alt) — идёшь вперёд, камера смотрит куда угодно", Category.VISUALS);
		setKeybind(org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT);
	}

	@Override
	public void onTick() {
		if (!isEnabled()) {
			return;
		}
		boolean held = getKeybind() > 0 && KeyManager.isDown(getKeybind());
		DarknessClient.tickFreeLook(held && mc.screen == null, getKeybind());
	}

	@Override
	protected void onDisable() {
		DarknessClient.tickFreeLook(false, -1);
	}

	@Override
	public String getDisplayInfo() {
		return DarknessClient.isFreeLookActive() ? "активен" : null;
	}
}
