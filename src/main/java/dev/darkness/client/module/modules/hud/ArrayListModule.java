package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArrayListModule extends HudModule {
	private final BooleanSetting showCombat;
	private final BooleanSetting showVisuals;
	private final BooleanSetting showHud;
	private final BooleanSetting showOptimization;
	private final BooleanSetting showUtility;

	private transient List<Module> cache = new ArrayList<>();
	private transient int cachedCount = -1;
	private transient long lastRebuild = 0;

	public ArrayListModule() {
		super("ArrayList", "Список включённых модулей");
		showCombat = new BooleanSetting("Combat", "Показывать боевые", true);
		showVisuals = new BooleanSetting("Visuals", "Показывать визуальные", true);
		showHud = new BooleanSetting("HUD", "Показывать HUD-модули", false);
		showOptimization = new BooleanSetting("Optimization", "Показывать оптимизацию", true);
		showUtility = new BooleanSetting("Utility", "Показывать утилиты", true);
		register(showCombat, showVisuals, showHud, showOptimization, showUtility);
	}

	private boolean visible(Module m) {
		return switch (m.getCategory()) {
			case COMBAT -> showCombat.get();
			case VISUALS -> showVisuals.get();
			case HUD -> showHud.get();
			case OPTIMIZATION -> showOptimization.get();
			case UTILITY -> showUtility.get();
		};
	}

	private List<Module> snapshot() {
		List<Module> all = dev.darkness.client.DarknessClient.getModuleManager().getEnabledModules();
		if (all.size() != cachedCount || System.currentTimeMillis() - lastRebuild > 1000) {
			List<Module> out = new ArrayList<>();
			for (Module m : all) {
				if (visible(m)) out.add(m);
			}
			out.sort(Comparator.comparingInt((Module m) -> {
				String s = m.getName() + (m.getDisplayInfo() != null ? " " + m.getDisplayInfo() : "");
				return mc.font.width(s);
			}).reversed());
			cache = out;
			cachedCount = all.size();
			lastRebuild = System.currentTimeMillis();
		}
		return cache;
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		List<Module> enabled = snapshot();
		int w = 30;
		for (var m : enabled) {
			String s = m.getName() + (m.getDisplayInfo() != null ? " " + m.getDisplayInfo() : "");
			w = Math.max(w, mc.font.width(s) + 8);
		}
		int h = Math.max(1, enabled.size()) * 11;
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
