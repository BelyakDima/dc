package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;

public class WorldCustomizerModule extends Module {
	public final BooleanSetting customSky;
	public final ColorSetting skyColor;
	public final BooleanSetting customFog;
	public final ColorSetting fogColor;
	public final BooleanSetting hideSunMoon;

	public WorldCustomizerModule() {
		super("WorldCustomizer", "Кастомизация неба, тумана и светил", Category.VISUALS);
		customSky = new BooleanSetting("Свой цвет неба", "Заменить цвет неба", true);
		skyColor = new ColorSetting("Цвет неба", "Цвет неба", 0xFF1B2A4A);
		customFog = new BooleanSetting("Свой цвет тумана", "Заменить цвет тумана", false);
		fogColor = new ColorSetting("Цвет тумана", "Цвет тумана", 0xFF22334F);
		hideSunMoon = new BooleanSetting("Скрыть солнце/луну/звёзды", "Убрать светила с неба", false);
		register(customSky, skyColor, customFog, fogColor, hideSunMoon);
	}
}
