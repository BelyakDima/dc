package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.util.KeyManager;
import net.minecraft.client.OptionInstance;

public class ZoomModule extends Module {
	private final NumberSetting factor;
	private final BooleanSetting smooth;
	private double previousFov = -1;
	private boolean active = false;

	public ZoomModule() {
		super("Zoom", "Оптический зум (по зажатой клавише модуля)", Category.VISUALS);
		factor = new NumberSetting("Кратность", "Во сколько раз приближать", 4, 1.5, 10, 0.5);
		smooth = new BooleanSetting("Плавность", "Плавное приближение", true);
		register(factor, smooth);
		setKeybind(org.lwjgl.glfw.GLFW.GLFW_KEY_C);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.options == null) return;
		OptionInstance<Integer> fov = mc.options.fov();
		int normalFov = fov.get();
		boolean held = getKeybind() > 0 && KeyManager.isDown(getKeybind());

		if (held) {
			if (!active) {
				active = true;
				previousFov = normalFov;
			}
			int target = Math.max(1, (int) (previousFov / factor.get()));
			int current = fov.get();
			if (smooth.get()) {
				int next = current + Math.max(1, (target - current) / 3);
				fov.set(next > target ? Math.max(target, next) : target);
				if (current > target) fov.set(Math.max(target, current - Math.max(1, (current - target) / 4)));
			} else {
				fov.set(target);
			}
		} else if (active) {
			active = false;
			if (previousFov > 0) {
				fov.set((int) previousFov);
			}
			previousFov = -1;
		}
	}

	@Override
	protected void onDisable() {
		if (active && previousFov > 0 && mc.options != null) {
			mc.options.fov().set((int) previousFov);
		}
		active = false;
		previousFov = -1;
	}

	@Override
	public String getDisplayInfo() {
		return String.format("%.1fx", factor.get());
	}
}
