package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class EntityCountHudModule extends HudModule {
	public EntityCountHudModule() {
		super("EntityCount", "Сущностей в радиусе 32 блоков");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null || mc.player == null) {
			setSize(70, 12);
			return;
		}
		int n = 0;
		for (var e : mc.level.entitiesForRendering()) {
			if (e != mc.player && e.distanceTo(mc.player) <= 32) n++;
		}
		String text = "Рядом: " + n;
		setSize(mc.font.width(text) + 8, 12);
		int color = n > 40 ? 0xFFFFC107 : 0xFFE0E0E0;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
