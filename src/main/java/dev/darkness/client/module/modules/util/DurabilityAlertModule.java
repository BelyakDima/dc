package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class DurabilityAlertModule extends Module {
	private final NumberSetting threshold;
	private final long[] lastAlert = new long[5];
	private static final long ALERT_COOLDOWN = 30_000;

	public DurabilityAlertModule() {
		super("DurabilityAlert", "Предупреждает, когда броня/предмет почти сломан", Category.UTILITY);
		threshold = new NumberSetting("Порог, %", "Предупреждать при прочности ниже", 10, 1, 50, 1);
		register(threshold);
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		ItemStack[] items = {
			mc.player.getItemBySlot(EquipmentSlot.HEAD),
			mc.player.getItemBySlot(EquipmentSlot.CHEST),
			mc.player.getItemBySlot(EquipmentSlot.LEGS),
			mc.player.getItemBySlot(EquipmentSlot.FEET),
			mc.player.getMainHandItem()
		};
		String[] names = {"Шлем", "Нагрудник", "Штаны", "Ботинки", "Предмет в руке"};
		long now = System.currentTimeMillis();
		for (int i = 0; i < items.length; i++) {
			ItemStack st = items[i];
			if (st.isEmpty() || !st.isDamageableItem()) continue;
			int left = st.getMaxDamage() - st.getDamageValue();
			float ratio = (float) left / st.getMaxDamage();
			if (ratio * 100 <= threshold.get() && now - lastAlert[i] > ALERT_COOLDOWN) {
				lastAlert[i] = now;
				DarknessClient.getNotifications().show(
					"§e" + names[i] + ": §c" + left + " прочности!");
			}
		}
	}
}
