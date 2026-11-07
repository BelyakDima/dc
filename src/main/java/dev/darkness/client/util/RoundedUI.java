package dev.darkness.client.util;

import net.minecraft.client.gui.GuiGraphics;

/** Скруглённые тёмные панели в стиле PulseVisuals. */
public final class RoundedUI {
	public static final int BG = 0xE6111118;
	public static final int BORDER = 0x2E2E3A;
	public static final int ACCENT = 0xFF7C4DFF;
	public static final int TEXT = 0xFFF0F0F0;
	public static final int TEXT_DIM = 0xFF9E9E9E;
	public static final int ROW_H = 13;

	private RoundedUI() {
	}

	/** Панель со скруглёнными углами (радиус 4): тёмный фон + мягкая рамка. */
	public static void panel(GuiGraphics g, int x, int y, int w, int h) {
		// тело
		RenderUtil.rect(g, x, y + 2, w, h - 4, BG);
		RenderUtil.rect(g, x + 2, y, w - 4, h, BG);
		// углы 2x2 сглаживания
		RenderUtil.rect(g, x + 1, y + 1, w - 2, h - 2, BG);
		// рамка
		RenderUtil.rect(g, x + 2, y, w - 4, 1, BORDER);
		RenderUtil.rect(g, x + 2, y + h - 1, w - 4, 1, BORDER);
		RenderUtil.rect(g, x, y + 2, 1, h - 4, BORDER);
		RenderUtil.rect(g, x + w - 1, y + 2, 1, h - 4, BORDER);
	}

	/** Заголовок панели: акцентный квадратик + имя. */
	public static void header(GuiGraphics g, int x, int y, String title, int accent) {
		RenderUtil.rect(g, x + 4, y + 4, 7, 7, accent);
		RenderUtil.rect(g, x + 6, y + 6, 3, 3, 0xFF111118);
		RenderUtil.text(g, title, x + 14, y + 3, TEXT);
	}

	/** Строка «слева текст, справа значение». */
	public static void row(GuiGraphics g, int x, int y, int w, String left, String right, int leftColor, int rightColor) {
		RenderUtil.text(g, left, x + 6, y + 2, leftColor);
		RenderUtil.textRight(g, right, x + w - 6, y + 2, rightColor);
	}

	/** Пилюля-переключатель PulseVisuals-стиля. */
	public static void togglePill(GuiGraphics g, int x, int y, boolean on) {
		int w = 22, h = 10;
		int bg = on ? ACCENT : 0xFF3A3A45;
		// скруглённая пилюля
		RenderUtil.rect(g, x + 2, y, w - 4, h, bg);
		RenderUtil.rect(g, x, y + 2, w, h - 4, bg);
		int knob = 0xFFE8E8F0;
		if (on) {
			RenderUtil.rect(g, x + w - 8, y + 1, 7, h - 2, knob);
			RenderUtil.rect(g, x + w - 9, y + 2, 9, h - 4, knob);
		} else {
			RenderUtil.rect(g, x + 1, y + 1, 7, h - 2, knob);
			RenderUtil.rect(g, x, y + 2, 9, h - 4, knob);
		}
	}
}
