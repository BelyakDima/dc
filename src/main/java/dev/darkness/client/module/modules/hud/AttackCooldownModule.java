package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class AttackCooldownModule extends HudModule {
	public AttackCooldownModule() {
		super("AttackCooldown", "Полоса готовности удара (как индикатор атаки)");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		float t = Math.min(1, mc.player.getAttackStrengthScale(1.0f));
		int w = 50, h = 4;
		setSize(w, h + 2);
		int x = getX(screenW), y = getY(screenH);
		RenderUtil.rect(g, x, y, w, h, 0x90000000);
		int color = t >= 1 ? 0xFF66BB6A : t > 0.6 ? 0xFFFFC107 : 0xFFF44336;
		RenderUtil.rect(g, x, y, (int) (w * t), h, color);
	}
}
