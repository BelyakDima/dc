package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public class ElytraSwapModule extends Module {
	private static final int CHEST_MENU_SLOT = 6;

	public ElytraSwapModule() {
		super("ElytraSwap", "Элитры в слот нагрудника одной кнопкой (V)", Category.UTILITY);
		setKeybind(GLFW.GLFW_KEY_V);
	}

	@Override
	public boolean isActionOnly() {
		return true;
	}

	@Override
	public void onKeyPressed() {
		if (isEnabled()) swap();
	}

	private void swap() {
		if (mc.player == null || mc.gameMode == null) return;
		var inv = mc.player.getInventory();
		ItemStack chest = mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);

		int elytraIdx = findItem(Items.ELYTRA, chest.is(Items.ELYTRA) ? -2 : -1);
		if (chest.is(Items.ELYTRA)) {
			// уже надеты: найти нагрудник и поменять местами
			int plateIdx = findChestplate();
			if (plateIdx < 0) {
				DarknessClient.getNotifications().show("§cНагрудник не найден в инвентаре");
				return;
			}
			click(menuSlotOf(plateIdx));
			click(CHEST_MENU_SLOT);
			click(menuSlotOf(plateIdx));
			DarknessClient.getNotifications().show("§aНадет нагрудник");
			return;
		}
		if (elytraIdx < 0) {
			DarknessClient.getNotifications().show("§cЭлитры не найдены в инвентаре");
			return;
		}
		click(menuSlotOf(elytraIdx));
		click(CHEST_MENU_SLOT);
		click(menuSlotOf(elytraIdx));
		DarknessClient.getNotifications().show("§aНадеты элитры");
	}

	private int findItem(net.minecraft.world.item.Item item, int excludeIdx) {
		for (int i = 0; i < 36; i++) {
			if (i == excludeIdx) continue;
			if (mc.player.getInventory().getItem(i).is(item)) return i;
		}
		return -1;
	}

	private int findChestplate() {
		for (int i = 0; i < 36; i++) {
			ItemStack st = mc.player.getInventory().getItem(i);
			var equippable = st.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
			if (equippable != null && equippable.slot() == net.minecraft.world.entity.EquipmentSlot.CHEST
				&& !st.is(Items.ELYTRA)) {
				return i;
			}
		}
		return -1;
	}

	static int menuSlotOf(int containerIdx) {
		return containerIdx < 9 ? 36 + containerIdx : containerIdx;
	}

	static void click(int menuSlot) {
		dev.darkness.client.DarknessClient.minecraftClick(menuSlot);
	}
}
