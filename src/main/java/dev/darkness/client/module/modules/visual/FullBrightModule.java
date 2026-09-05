package dev.darkness.client.module.modules.visual;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.mixin.OptionInstanceAccessor;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.client.OptionInstance;

public class FullBrightModule extends Module {
	private Double previousGamma = null;

	public FullBrightModule() {
		super("FullBright", "Максимальная яркость (гамма 15)", Category.VISUALS);
	}

	@Override
	protected void onEnable() {
		if (mc.options == null) return;
		OptionInstance<Double> gamma = mc.options.gamma();
		previousGamma = gamma.get();
		((OptionInstanceAccessor) (Object) gamma).darkness$setValue((Object) 15.0);
	}

	@Override
	protected void onDisable() {
		if (mc.options == null || previousGamma == null) return;
		OptionInstance<Double> gamma = mc.options.gamma();
		((OptionInstanceAccessor) (Object) gamma).darkness$setValue((Object) previousGamma);
		previousGamma = null;
	}
}
