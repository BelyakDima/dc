package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.ModeSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class CrosshairModule extends Module {
	private final ModeSetting style;
	private final ColorSetting color;
	private final NumberSetting gap;
	private final NumberSetting length;
	private final NumberSetting thickness;
	private final dev.darkness.client.module.BooleanSetting dynamic;

	public CrosshairModule() {
		super("CustomCrosshair", "Кастомный прицел вместо стандартного", Category.VISUALS);
		style = new ModeSetting("Стиль", "Форма прицел", java.util.List.of("Крест", "Точка", "Крест+точка"), "Крест");
		color = new ColorSetting("Цвет", "Цвет прицела", 0xFF7C4DFF);
		gap = new NumberSetting("Отступ", "Расстояние от центра", 3, 0, 10, 1);
		length = new NumberSetting("Длина", "Длина лучей", 5, 1, 12, 1);
		thickness = new NumberSetting("Толщина", "Толщина линий", 2, 1, 5, 1);
		dynamic = new dev.darkness.client.module.BooleanSetting("Динамика", "Расходится при движении/атаке", true);
		register(style, color, gap, length, thickness, dynamic);
	}

	public void render(GuiGraphics g, int screenW, int screenH) {
		int cx = screenW / 2, cy = screenH / 2;
		int c = color.rgb();
		int t = (int) Math.round(thickness.get());
		int l = (int) Math.round(length.get());
		int gp = (int) Math.round(gap.get());

		float dyn = 0;
		if (dynamic.get() && mc.player != null) {
			float speed = (float) Math.sqrt(mc.player.getDeltaMovement().horizontalDistanceSqr());
			float atk = mc.player.getAttackStrengthScale(1.0f);
			dyn = Math.min(4, speed * 14) + (1 - atk) * 4;
			gp += (int) dyn;
		}

		if (style.is("Крест") || style.is("Крест+точка")) {
			RenderUtil.rect(g, cx - gp - l, cy - t / 2, l, t, c);
			RenderUtil.rect(g, cx + gp, cy - t / 2, l, t, c);
			RenderUtil.rect(g, cx - t / 2, cy - gp - l, t, l, c);
			RenderUtil.rect(g, cx - t / 2, cy + gp, t, l, c);
		}
		if (style.is("Точка") || style.is("Крест+точка")) {
			RenderUtil.rect(g, cx - t / 2, cy - t / 2, t, t, c);
		}
	}
}
