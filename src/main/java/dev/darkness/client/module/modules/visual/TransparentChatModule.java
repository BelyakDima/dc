package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.client.OptionInstance;

public class TransparentChatModule extends Module {
	private Double previous = null;

	public TransparentChatModule() {
		super("TransparentChat", "Полупрозрачный фон чата", Category.VISUALS);
	}

	@Override
	protected void onEnable() {
		OptionInstance<Double> op = mc.options.chatOpacity();
		previous = op.get();
		op.set(0.0);
	}

	@Override
	protected void onDisable() {
		if (previous == null) return;
		mc.options.chatOpacity().set(previous);
		previous = null;
	}
}
