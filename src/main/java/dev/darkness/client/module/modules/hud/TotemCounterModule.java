package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TotemCounterModule extends HudModule {
	public TotemCounterModule() {
		super("TotemCounter", "Сколько тотемов в инвентаре");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		int count = 0;
		var inv = mc.player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) count++;
		}
		String text = "Тотемы: " + count;
		setSize(mc.font.width(text) + 8, 12);
		int color = count == 0 ? 0xFFF44336 : count < 3 ? 0xFFFFC107 : 0xFF66BB6A;
		RenderUtil.text(g, text, getX(screenW), getY(screenH), color);
	}
}
