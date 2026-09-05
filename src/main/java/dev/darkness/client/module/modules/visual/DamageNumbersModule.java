package dev.darkness.client.module.modules.visual;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class DamageNumbersModule extends Module {
	private final ColorSetting normalColor;
	private final ColorSetting critColor;
	private final BooleanSetting showCrits;

	public DamageNumbersModule() {
		super("DamageNumbers", "Всплывающий урон над целью", Category.VISUALS);
		normalColor = new ColorSetting("Цвет урона", "Цвет обычного урона", 0xFFFFD54F);
		critColor = new ColorSetting("Цвет крита", "Цвет критического урона", 0xFFFF5252);
		showCrits = new BooleanSetting("Выделять криты", "Другой цвет для критов", true);
		register(normalColor, critColor, showCrits);
	}

	public void renderOverlay(GuiGraphics g) {
		var list = DarknessClient.getTargetManager().getDamageNumbers();
		for (var n : list) {
			float[] pos = RenderUtil.worldToScreen(n.x, n.y + n.offset, n.z);
			if (pos == null) continue;
			long age = System.currentTimeMillis() - n.created;
			float lifeT = Math.min(1, age / 1200f);
			int alpha = lifeT < 0.7f ? 255 : (int) (255 * (1 - (lifeT - 0.7f) / 0.3f));
			int color = showCrits.get() && n.crit ? critColor.rgb() : normalColor.rgb();
			color = RenderUtil.withAlpha(color, Math.max(0, alpha));
			String text = String.format("%.1f", n.amount);
			RenderUtil.textCentered(g, text, (int) pos[0], (int) pos[1], color);
		}
	}
}
