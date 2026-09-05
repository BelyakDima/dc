package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArrowCountModule extends HudModule {
	public ArrowCountModule() {
		super("ArrowCount", "Количество стрел при натянутом луке");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		ItemStack main = mc.player.getMainHandItem();
		if (!(main.getItem() instanceof net.minecraft.world.item.BowItem)
			&& !(main.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
			setSize(40, 12);
			return;
		}
		int count = 0;
		var inv = mc.player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack st = inv.getItem(i);
			if (st.is(Items.ARROW) || st.is(Items.SPECTRAL_ARROW) || st.is(Items.TIPPED_ARROW)) {
				count += st.getCount();
			}
		}
		String text = "Стрелы: " + count;
		setSize(mc.font.width(text) + 8, 12);
		int color = count == 0 ? 0xFFF44336 : 0xFFE0E0E0;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
