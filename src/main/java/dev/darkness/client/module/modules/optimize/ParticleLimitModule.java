package dev.darkness.client.module.modules.optimize;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;

public class ParticleLimitModule extends Module {
	private final NumberSetting max;

	public ParticleLimitModule() {
		super("ParticleLimit", "Ограничивает количество частиц (прирост FPS)", Category.OPTIMIZATION);
		max = new NumberSetting("Максимум", "Максимальное количество частиц в тик", 300, 0, 2000, 50);
		register(max);
	}

	public NumberSetting getMaxSetting() {
		return max;
	}

	@Override
	public String getDisplayInfo() {
		return String.valueOf((int) Math.round(max.get()));
	}
}
