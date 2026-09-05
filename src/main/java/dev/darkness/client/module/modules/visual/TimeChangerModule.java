package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.ModeSetting;
import dev.darkness.client.module.Module;

public class TimeChangerModule extends Module {
	private final ModeSetting time;

	public TimeChangerModule() {
		super("TimeChanger", "Визуально меняет положение солнца и луны (время сервера не трогает)", Category.VISUALS);
		time = new ModeSetting("Время", "Какое время показать",
			java.util.List.of("День", "Закат", "Ночь", "Полночь"), "День");
		register(time);
	}

	/** Целевое игровое время выбранного режима. */
	public int targetTime() {
		return switch (time.get()) {
			case "Закат" -> 12500;
			case "Ночь" -> 15500;
			case "Полночь" -> 18000;
			default -> 1000;
		};
	}
}
