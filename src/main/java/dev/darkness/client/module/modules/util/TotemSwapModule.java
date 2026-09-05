package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public class TotemSwapModule extends Module {
	private static final int OFFHAND_MENU_SLOT = 45;

	public TotemSwapModule() {
		super("TotemSwap", "Тотем в левую руку одной кнопкой (G)", Category.UTILITY);
		setKeybind(GLFW.GLFW_KEY_G);
	}

	@Override
	public void onKeyPressed() {
		if (isEnabled()) swap();
	}

	private void swap() {
		if (mc.player == null || mc.gameMode == null) return;
		if (mc.player.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.TOTEM_OF_UNDYING)) {
			DarknessClient.getNotifications().show("Тотем уже в левой руке");
			return;
		}
		int totemIdx = -1;
		for (int i = 0; i < 36; i++) {
			if (mc.player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) {
				totemIdx = i;
				break;
			}
		}
		if (totemIdx < 0) {
			DarknessClient.getNotifications().show("§cТотемов нет в инвентаре");
			return;
		}
		int menuSlot = ElytraSwapModule.menuSlotOf(totemIdx);
		dev.darkness.client.DarknessClient.minecraftClick(menuSlot);
		dev.darkness.client.DarknessClient.minecraftClick(OFFHAND_MENU_SLOT);
		dev.darkness.client.DarknessClient.minecraftClick(menuSlot);
		DarknessClient.getNotifications().show("§aТотем в левой руке");
	}
}
