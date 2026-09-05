package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.TargetManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class HitSoundModule extends Module {
	private final ColorSetting ignore;

	public HitSoundModule() {
		super("HitSound", "Звуковой сигнал при попадании по цели", Category.UTILITY);
		ignore = new ColorSetting("Цвет", "Не используется, оставлено для совместимости", 0xFF7C4DFF);
		register(ignore);
	}

	private final TargetManager.DamageListener listener = (amount, x, y, z, crit) -> {
		if (!isEnabled() || mc.player == null) return;
		mc.getSoundManager().play(SimpleSoundInstance.forUI(
			SoundEvents.EXPERIENCE_ORB_PICKUP, crit ? 1.6f : 1.2f, 0.6f));
	};

	@Override
	protected void onEnable() {
		DarknessClient.getTargetManager().addListener(listener);
	}

	@Override
	protected void onDisable() {
		DarknessClient.getTargetManager().removeListener(listener);
	}
}
