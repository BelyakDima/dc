package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.KeyManager;
import org.lwjgl.glfw.GLFW;

public class ToggleSneakModule extends Module {
	private boolean toggleState = false;
	private boolean prevPhysical = false;

	public ToggleSneakModule() {
		super("ToggleSneak", "Shift переключает режим крадучись (не надо держать)", Category.UTILITY);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.options == null) {
			toggleState = false;
			prevPhysical = false;
			return;
		}
		boolean physical = KeyManager.isDown(GLFW.GLFW_KEY_LEFT_SHIFT) || KeyManager.isDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
		if (physical && !prevPhysical) {
			toggleState = !toggleState;
		}
		prevPhysical = physical;
		mc.options.keyShift.setDown(physical || toggleState);
	}

	@Override
	protected void onDisable() {
		toggleState = false;
		if (mc.options != null) {
			mc.options.keyShift.setDown(false);
		}
	}

	@Override
	public String getDisplayInfo() {
		return toggleState ? "on" : null;
	}
}
