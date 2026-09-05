package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class CpsModule extends HudModule {
	private final BooleanSetting both;

	public CpsModule() {
		super("CPS", "Клики в секунду");
		both = new BooleanSetting("Обе кнопки", "Показывать ЛКМ и ПКМ", true);
		register(both);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		dev.darkness.client.util.CpManager cp = dev.darkness.client.DarknessClient.getCpManager();
		String text = both.get()
			? "CPS: " + cp.leftCps() + " | " + cp.rightCps()
			: "CPS: " + cp.leftCps();
		setSize(mc.font.width(text) + 8, 12);
		RenderUtil.text(g, text, getX(screenW), getY(screenH), 0xFFE0E0E0);
	}
}
