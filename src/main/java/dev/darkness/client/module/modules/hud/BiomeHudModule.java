package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.Locale;

public class BiomeHudModule extends HudModule {
	public BiomeHudModule() {
		super("BiomeHUD", "Название текущего биома");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null || mc.player == null) {
			setSize(70, 12);
			return;
		}
		String name = "?";
		try {
			Holder<Biome> holder = mc.level.getBiome(mc.player.blockPosition());
			name = holder.unwrapKey()
				.map(k -> k.identifier().getPath())
				.orElse("unknown");
			if (name.isEmpty()) name = "?";
			name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1).replace('_', ' ');
		} catch (Exception ignored) {
		}
		String text = "Биом: " + name;
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
