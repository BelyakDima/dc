package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class InventoryHudModule extends HudModule {
	private final BooleanSetting showHotbar;
	private final BooleanSetting durability;

	public InventoryHudModule() {
		super("InventoryHUD", "Инвентарь игрока как элемент интерфейса");
		showHotbar = new BooleanSetting("Хотбар", "Показывать панель быстрого доступа", true);
		durability = new BooleanSetting("Прочность", "Числа прочности на предметах", true);
		register(showHotbar, durability);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		var inv = mc.player.getInventory();
		int x = getX(screenW), y = getY(screenH);
		int slot = 18;
		int rows = 3, cols = 9;
		boolean hot = showHotbar.get();
		int w = cols * slot + 2;
		int h = rows * slot + (hot ? slot + 4 : 0) + 2;
		setSize(w, h);

		RenderUtil.rect(g, x, y, w, h, 0x900D0D14);
		RenderUtil.outline(g, x, y, w, h, 0x40FFFFFF);

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int invIdx = 9 + row * 9 + col;
				int sx = x + 1 + col * slot;
				int sy = y + 1 + row * slot;
				RenderUtil.rect(g, sx, sy, slot - 1, slot - 1, 0x50161622);
				ItemStack st = inv.getItem(invIdx);
				if (!st.isEmpty()) {
					g.renderItem(st, sx + 1, sy + 1);
					if (durability.get() && st.isDamageableItem()) {
						int left = st.getMaxDamage() - st.getDamageValue();
						RenderUtil.textCentered(g, String.valueOf(left), sx + slot / 2, sy + 10,
							ArmorHudModule.healthColor((float) left / st.getMaxDamage()));
					}
				}
			}
		}
		if (hot) {
			int hy = y + 1 + rows * slot + 3;
			for (int col = 0; col < cols; col++) {
				int sx = x + 1 + col * slot;
				RenderUtil.rect(g, sx, hy, slot - 1, slot - 1, 0x50161622);
				ItemStack st = inv.getItem(col);
				if (!st.isEmpty()) {
					g.renderItem(st, sx + 1, hy + 1);
					if (durability.get() && st.isDamageableItem()) {
						int left = st.getMaxDamage() - st.getDamageValue();
						RenderUtil.textCentered(g, String.valueOf(left), sx + slot / 2, hy + 10,
							ArmorHudModule.healthColor((float) left / st.getMaxDamage()));
					}
				}
			}
		}
	}
}
