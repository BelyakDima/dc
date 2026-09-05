package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.client.OptionInstance;

public class NoViewBobModule extends Module {
	private Boolean previous = null;

	public NoViewBobModule() {
		super("NoViewBob", "Отключает покачивание камеры при ходьбе", Category.VISUALS);
	}

	@Override
	protected void onEnable() {
		if (mc.options == null) return;
		previous = mc.options.bobView().get();
		mc.options.bobView().set(false);
	}

	@Override
	protected void onDisable() {
		if (mc.options == null || previous == null) return;
		mc.options.bobView().set(previous);
		previous = null;
	}
}
