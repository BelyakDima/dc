package dev.darkness.client.module.modules.util;

import com.mojang.blaze3d.platform.InputConstants;
import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.util.KeyManager;
import net.minecraft.client.KeyMapping;

public class AutoClickerModule extends Module {
	private final NumberSetting cps;
	private long nextClick = 0;

	public AutoClickerModule() {
		super("AutoClicker", "Автоклик при зажатой клавише модуля (по умолчанию R)", Category.UTILITY);
		cps = new NumberSetting("CPS", "Кликов в секунду", 10, 1, 20, 1);
		register(cps);
		setKeybind(org.lwjgl.glfw.GLFW.GLFW_KEY_R);
	}

	@Override
	public boolean isActionOnly() {
		return true;
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.screen != null) return;
		if (!isEnabled() || getKeybind() <= 0) return;
		if (!KeyManager.isDown(getKeybind())) return;
		long now = System.currentTimeMillis();
		if (now >= nextClick) {
			KeyMapping.click(InputConstants.getKey("key.keyboard.mouse.left"));
			nextClick = now + (long) (1000.0 / cps.get());
		}
	}
}
