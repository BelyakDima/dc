package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;

public class CustomHandModule extends Module {
	public final NumberSetting offsetX;
	public final NumberSetting offsetY;
	public final NumberSetting scale;

	public CustomHandModule() {
		super("CustomHand", "Настройка вида руки от первого лица", Category.VISUALS);
		offsetX = new NumberSetting("Смещение X", "Влево/вправо", 0, -1, 1, 0.05);
		offsetY = new NumberSetting("Смещение Y", "Выше/ниже", 0, -1, 1, 0.05);
		scale = new NumberSetting("Масштаб", "Размер руки", 1, 0.3, 2, 0.05);
		register(offsetX, offsetY, scale);
	}
}
