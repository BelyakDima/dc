package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Locale;

public class CoordinatesModule extends HudModule {
	private final BooleanSetting direction;

	public CoordinatesModule() {
		super("Coordinates", "Текущие координаты XYZ");
		direction = new BooleanSetting("Направление", "Показывать сторону света", true);
		register(direction);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) {
			setSize(80, 12);
			return;
		}
		String dir = direction.get() ? "  " + facing() : "";
		String text = String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f%s",
			mc.player.getX(), mc.player.getY(), mc.player.getZ(), dir);
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}

	private String facing() {
		if (mc.player == null) return "";
		float yaw = mc.player.getYRot();
		String[] dirs = {"юг", "запад", "север", "восток"};
		int idx = Math.floorMod(Math.round(yaw / 90f), 4);
		return dirs[idx];
	}
}
