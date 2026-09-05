package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.ModeSetting;
import dev.darkness.client.module.Module;

import java.util.List;

public class AspectRatioModule extends Module {
	public AspectRatioModule() {
		super("AspectRatio", "Растянутый мир под выбранное соотношение сторон (GUI не трогается)", Category.VISUALS);
		register(new ModeSetting("Соотношение", "Целевое соотношение сторон",
			List.of("16:9", "16:10", "21:9", "4:3", "5:4", "1:1"), "4:3"));
	}
}
