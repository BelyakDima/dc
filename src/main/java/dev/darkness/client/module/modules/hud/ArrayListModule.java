package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ArrayListModule extends HudModule {
	public ArrayListModule() {
		super("ArrayList", "Список включённых модулей");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		List<dev.darkness.client.module.Module> enabled = dev.darkness.client.DarknessClient.getModuleManager().getEnabledModules();
		enabled.sort((a, b) -> Integer.compare(
			mc.font.width(b.getName() + (b.getDisplayInfo() != null ? " " + b.getDisplayInfo() : "")),
			mc.font.width(a.getName() + (a.getDisplayInfo() != null ? " " + a.getDisplayInfo() : ""))));
		int w = 0;
		for (var m : enabled) {
			String s = m.getName() + (m.getDisplayInfo() != null ? " " + m.getDisplayInfo() : "");
			w = Math.max(w, mc.font.width(s) + 8);
		}
		w = Math.max(w, 30);
		int h = enabled.size() * 11;
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		int i = 0;
		for (var m : enabled) {
			String info = m.getDisplayInfo();
			String label = m.getName();
			float hue = (System.currentTimeMillis() % 6000) / 6000f + i * 0.02f;
			int color = java.awt.Color.HSBtoRGB(hue % 1.0f, 0.55f, 1.0f) | 0xFF000000;
			RenderUtil.rect(g, x, y + i * 11, w, 11, 0x660D0D14);
			int textW = mc.font.width(label);
			int labelX = x + w - textW - 4;
			g.drawString(mc.font, label, labelX, y + i * 11 + 2, color, true);
			if (info != null) {
				g.drawString(mc.font, info, labelX - mc.font.width(info) - 2, y + i * 11 + 2, 0xFFB0B0B0, true);
			}
			RenderUtil.rect(g, x + w - 1, y + i * 11, 1, 11, color);
			i++;
		}
	}
}
