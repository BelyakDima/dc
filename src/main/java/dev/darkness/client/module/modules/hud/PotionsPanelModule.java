package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import dev.darkness.client.util.RoundedUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

/** Панель активных эффектов (Potions) в стиле PulseVisuals. */
public class PotionsPanelModule extends HudModule {
	private final BooleanSetting hideInfinite;

	public PotionsPanelModule() {
		super("Potions", "Активные эффекты в панель");
		hideInfinite = new BooleanSetting("Скрыть ∞", "Не показывать бесконечные эффекты", false);
		register(hideInfinite);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) {
			setSize(120, 22);
			return;
		}
		List<MobEffectInstance> effects = new ArrayList<>();
		for (MobEffectInstance e : mc.player.getActiveEffects()) {
			if (hideInfinite.get() && e.isInfiniteDuration()) continue;
			effects.add(e);
		}
		int w = 132;
		int h = 20 + effects.size() * RoundedUI.ROW_H;
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		RoundedUI.panel(g, x, y, w, h);
		RoundedUI.header(g, x, y, "Potions", 0xFFB388FF);
		int ry = y + 18;
		if (effects.isEmpty()) {
			RenderUtil.text(g, "§7нет эффектов", x + 6, ry + 2, 0xFF757575);
			return;
		}
		for (MobEffectInstance e : effects) {
			String name = e.getEffect().value().getDisplayName().getString();
			if (name.length() > 14) name = name.substring(0, 13);
			if (e.getAmplifier() > 0) name += " " + (e.getAmplifier() + 1);
			String dur = e.isInfiniteDuration() ? "∞"
				: fmt(e.getDuration() / 20);
			RoundedUI.row(g, x, ry, w, name, dur, 0xFFE0E0E0, 0xFFB0B0B0);
			ry += RoundedUI.ROW_H;
		}
	}

	private static String fmt(int sec) {
		if (sec >= 3600) return "**:**";
		return String.format("%d:%02d", sec / 60, sec % 60);
	}
}
