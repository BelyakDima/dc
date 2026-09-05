package dev.darkness.client.module;

import net.minecraft.client.gui.GuiGraphics;

/** Визуальные модули, рисующие поверх мира/экрана каждый кадр. */
public interface OverlayRenderer {
	void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick);
}
