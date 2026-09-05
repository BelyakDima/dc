package dev.darkness.client.module.modules.optimize;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.client.OptionInstance;

public class NoEntityShadowsModule extends Module {
	private Boolean previous = null;

	public NoEntityShadowsModule() {
		super("NoEntityShadows", "Отключает тени сущностей (прирост FPS)", Category.OPTIMIZATION);
	}

	@Override
	protected void onEnable() {
		if (mc.options == null) return;
		OptionInstance<Boolean> shadows = mc.options.entityShadows();
		previous = shadows.get();
		shadows.set(false);
	}

	@Override
	protected void onDisable() {
		if (mc.options == null || previous == null) return;
		mc.options.entityShadows().set(previous);
		previous = null;
	}
}
