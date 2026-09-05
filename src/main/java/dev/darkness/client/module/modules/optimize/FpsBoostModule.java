package dev.darkness.client.module.modules.optimize;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.client.CloudStatus;
import net.minecraft.server.level.ParticleStatus;

public class FpsBoostModule extends Module {
	private final BooleanSetting minParticles;
	private final BooleanSetting noClouds;
	private final BooleanSetting noVignette;

	private ParticleStatus prevParticles;
	private CloudStatus prevClouds;
	private Boolean prevVignette;

	public FpsBoostModule() {
		super("FpsBoost", "Быстрое применение FPS-настроек ваниллы", Category.OPTIMIZATION);
		minParticles = new BooleanSetting("Минимум частиц", "Particle status: minimal", true);
		noClouds = new BooleanSetting("Без облаков", "Отключить облака", true);
		noVignette = new BooleanSetting("Без виньетки", "Отключить виньетку", false);
		register(minParticles, noClouds, noVignette);
	}

	@Override
	protected void onEnable() {
		if (mc.options == null) return;
		prevParticles = mc.options.particles().get();
		prevClouds = mc.options.getCloudsType();
		prevVignette = mc.options.vignette().get();
		apply();
	}

	private void apply() {
		if (mc.options == null) return;
		if (minParticles.get()) mc.options.particles().set(ParticleStatus.MINIMAL);
		if (noClouds.get()) mc.options.cloudStatus().set(CloudStatus.OFF);
		if (noVignette.get()) mc.options.vignette().set(false);
	}

	@Override
	public void onTick() {
		apply();
	}

	@Override
	protected void onDisable() {
		if (mc.options == null) return;
		if (prevParticles != null) mc.options.particles().set(prevParticles);
		if (prevClouds != null) mc.options.cloudStatus().set(prevClouds);
		if (prevVignette != null) mc.options.vignette().set(prevVignette);
	}
}
