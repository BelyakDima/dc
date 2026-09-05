package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Locale;

public class SpeedometerModule extends HudModule {
	private double prevX = 0, prevZ = 0;
	private float smoothed = 0;
	private boolean first = true;

	public SpeedometerModule() {
		super("Speedometer", "Текущая скорость движения (блоков/с)");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		String text = String.format(Locale.ROOT, "%.1f b/s", smoothed);
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}

	@Override
	public void onTick() {
		if (mc.player == null) {
			first = true;
			smoothed = 0;
			return;
		}
		if (first) {
			prevX = mc.player.getX();
			prevZ = mc.player.getZ();
			first = false;
			return;
		}
		double dx = mc.player.getX() - prevX;
		double dz = mc.player.getZ() - prevZ;
		prevX = mc.player.getX();
		prevZ = mc.player.getZ();
		float bps = (float) Math.sqrt(dx * dx + dz * dz) * 20f;
		smoothed = smoothed * 0.85f + bps * 0.15f;
	}
}
