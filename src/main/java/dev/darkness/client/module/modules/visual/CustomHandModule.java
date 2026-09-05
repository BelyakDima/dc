package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ModeSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;

import java.util.List;

public class CustomHandModule extends Module {
	public final BooleanSetting noSway;
	public final ModeSetting swingStyle;
	// правая (основная) рука
	public final NumberSetting rightX;
	public final NumberSetting rightY;
	public final NumberSetting rightScale;
	// левая (вторая) рука
	public final NumberSetting leftX;
	public final NumberSetting leftY;
	public final NumberSetting leftScale;

	public CustomHandModule() {
		super("CustomHand", "Тонкая настройка рук от первого лица", Category.VISUALS);
		noSway = new BooleanSetting("Отключить тряску", "Руки не дрожат при ходьбе и беге", true);
		swingStyle = new ModeSetting("Анимация удара", "Стиль замаха руки",
			List.of("Ванилла", "Быстрая", "Плавная", "Медленная", "Дуга"), "Ванилла");
		rightX = new NumberSetting("Правая: X", "Смещение правой руки влево/вправо", 0, -1, 1, 0.05);
		rightY = new NumberSetting("Правая: Y", "Смещение правой руки выше/ниже", 0, -1, 1, 0.05);
		rightScale = new NumberSetting("Правая: масштаб", "Размер правой руки", 1, 0.05, 2, 0.05);
		leftX = new NumberSetting("Левая: X", "Смещение левой руки влево/вправо", 0, -1, 1, 0.05);
		leftY = new NumberSetting("Левая: Y", "Смещение левой руки выше/ниже", 0, -1, 1, 0.05);
		leftScale = new NumberSetting("Левая: масштаб", "Размер левой руки", 1, 0.05, 2, 0.05);
		register(noSway, swingStyle, rightX, rightY, rightScale, leftX, leftY, leftScale);
	}

	/** Кривая замаха: превращает линейный прогресс удара в выбранный стиль. */
	public static float applySwingStyle(String mode, float t) {
		t = Math.max(0, Math.min(1, t));
		return switch (mode) {
			case "Быстрая" -> (float) Math.pow(t, 0.55);
			case "Плавная" -> t * t * (3 - 2 * t);
			case "Медленная" -> (float) Math.pow(t, 1.6);
			case "Дуга" -> (float) (Math.sin(t * Math.PI) * 0.65 + t * 0.35);
			default -> t;
		};
	}
}
