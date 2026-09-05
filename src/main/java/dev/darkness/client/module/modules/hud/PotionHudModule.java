package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public class PotionHudModule extends HudModule {
	public PotionHudModule() {
		super("PotionHUD", "Активные эффекты зелий");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		int x = getX(screenW), y = getY(screenH);
		int i = 0;
		for (MobEffectInstance effect : mc.player.getActiveEffects()) {
			int dur = effect.getDuration() / 20;
			String time = dur > 3600 ? "**:**" : String.format("%d:%02d", dur / 60, dur % 60);
			String name = effect.getEffect().value().getDisplayName().getString();
			if (name.length() > 16) name = name.substring(0, 16);
			int amp = effect.getAmplifier();
			String label = name + (amp > 0 ? " " + (amp + 1) : "") + " §8" + time;
			int w = mc.font.width(name + (amp > 0 ? " " + (amp + 1) : "") + " " + time) + 22;
			RenderUtil.rect(g, x, y + i * 16, w, 15, 0x900D0D14);
			RenderUtil.text(g, name + (amp > 0 ? " " + (amp + 1) : ""), x + 20, y + i * 16 + 4, 0xFFE0E0E0);
			RenderUtil.textRight(g, time, x + w - 3, y + i * 16 + 4, 0xFF9E9E9E);
			i++;
		}
		setSize(70, Math.max(1, i) * 16);
	}
}
