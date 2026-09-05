package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GappleCounterModule extends HudModule {
	public GappleCounterModule() {
		super("GappleCounter", "Сколько золотых яблок в инвентаре");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		int count = 0;
		var inv = mc.player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack st = inv.getItem(i);
			if (st.is(Items.GOLDEN_APPLE) || st.is(Items.ENCHANTED_GOLDEN_APPLE)) {
				count += st.getCount();
			}
		}
		String text = "Голды: " + count;
		setSize(mc.font.width(text) + 8, 12);
		int color = count == 0 ? 0xFFF44336 : 0xFFFFD54F;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
