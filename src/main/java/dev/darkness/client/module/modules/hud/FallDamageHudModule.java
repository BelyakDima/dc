package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class FallDamageHudModule extends HudModule {
	public FallDamageHudModule() {
		super("FallDamageHUD", "Предсказание урона от падения");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null || mc.player.fallDistance <= 3) {
			setSize(60, 12);
			return;
		}
		float hearts = (float) mc.player.fallDistance - 3;
		String text = String.format("Падение: ~%.1f ❤", hearts);
		setSize(mc.font.width(text) + 8, 12);
		int color = hearts > 6 ? 0xFFF44336 : hearts > 3 ? 0xFFFFC107 : 0xFF66BB6A;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
