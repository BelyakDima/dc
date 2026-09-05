package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.world.item.Items;

public class TotemPopAlertModule extends Module {
	private boolean prevTotemInOffhand = false;

	public TotemPopAlertModule() {
		super("TotemPopAlert", "Уведомление, когда ваш тотем лопнул", Category.UTILITY);
	}

	@Override
	public void onTick() {
		if (mc.player == null) {
			prevTotemInOffhand = false;
			return;
		}
		boolean now = mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND).is(Items.TOTEM_OF_UNDYING);
		if (prevTotemInOffhand && !now && mc.player.hurtTime > 0) {
			DarknessClient.getNotifications().show("§dТотем лопнул! §fНайдите новый");
		}
		prevTotemInOffhand = now;
	}
}
