package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

public class BowChargeHudModule extends HudModule {
	public BowChargeHudModule() {
		super("BowCharge", "Сила натяжения лука в процентах");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null || !mc.player.isUsingItem()) {
			setSize(50, 12);
			return;
		}
		ItemStack use = mc.player.getUseItem();
		if (!(use.getItem() instanceof BowItem)) {
			setSize(50, 12);
			return;
		}
		float power = BowItem.getPowerForTime(mc.player.getUseItemRemainingTicks());
		if (power < 0.05f) {
			setSize(50, 12);
			return;
		}
		String text = String.format("Лук: %.0f%%", power * 100);
		setSize(mc.font.width(text) + 8, 12);
		int color = power >= 1 ? 0xFF66BB6A : 0xFFE0E0E0;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
