package dev.darkness.client.module.modules.visual;

import dev.darkness.client.mixin.OptionInstanceAccessor;
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
		super("Zoom", "Оптический зум (зажать клавишу модуля)", Category.VISUALS);
		factor = new NumberSetting("Кратность", "Во сколько раз приближать", 4, 1.5, 10, 0.5);
		smooth = new BooleanSetting("Плавность", "Плавное приближение и возврат", true);
		register(factor, smooth);
		setKeybind(org.lwjgl.glfw.GLFW.GLFW_KEY_C);
	}

	/** Прямая запись значения OptionInstance — ванильный set() клампит FOV в 30..110. */
	private static void setRawFov(OptionInstance<Integer> fov, double value) {
		int v = Math.max(1, (int) Math.round(value));
		((OptionInstanceAccessor) (Object) fov).darkness$setValue(v);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.options == null) return;
		OptionInstance<Integer> fov = mc.options.fov();
		boolean held = getKeybind() > 0 && KeyManager.isDown(getKeybind());

		if (held) {
			if (!active) {
				active = true;
				previousFov = fov.get();
			}
			double target = Math.max(1, previousFov / factor.get());
			double cur = fov.get();
			if (smooth.get()) {
				double next = cur + (target - cur) * 0.5;
				if (Math.abs(next - target) < 0.75) next = target;
				setRawFov(fov, next);
			} else {
				setRawFov(fov, target);
			}
		} else if (active) {
			double cur = fov.get();
			double target = previousFov;
			if (smooth.get()) {
				double next = cur + (target - cur) * 0.5;
				if (Math.abs(next - target) < 0.75) {
					setRawFov(fov, target);
					active = false;
					previousFov = -1;
				} else {
					setRawFov(fov, next);
				}
			} else {
				setRawFov(fov, target);
				active = false;
				previousFov = -1;
			}
		}
	}

	/** Восстановить FOV немедленно (вызывается при выходе из мира). */
	public void reset() {
		onDisable();
	}

	@Override
	protected void onDisable() {
		if (active && previousFov > 0 && mc.options != null) {
			setRawFov(mc.options.fov(), previousFov);
		}
		active = false;
		previousFov = -1;
	}

	@Override
	public String getDisplayInfo() {
		return String.format("%.1fx", factor.get());
	}
}
