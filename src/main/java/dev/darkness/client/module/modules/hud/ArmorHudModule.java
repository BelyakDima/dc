package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ArmorHudModule extends HudModule {
	private final BooleanSetting durability;

	public ArmorHudModule() {
		super("ArmorHUD", "Прочность брони и предметов");
		durability = new BooleanSetting("Прочность", "Показывать остаток прочности", true);
		register(durability);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		ItemStack[] stacks = {
			mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD),
			mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST),
			mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS),
			mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET),
			mc.player.getMainHandItem()
		};
		int x = getX(screenW), y = getY(screenH);
		int i = 0;
		for (ItemStack s : stacks) {
			if (s.isEmpty()) {
				i++;
				continue;
			}
			int yy = y + i * 20;
			g.renderItem(s, x, yy);
			float dmg = s.getMaxDamage() > 0 ? (float) (s.getMaxDamage() - s.getDamageValue()) / s.getMaxDamage() : 1f;
			if (durability.get() && s.isDamageableItem()) {
				String label = String.valueOf(s.getMaxDamage() - s.getDamageValue());
				RenderUtil.textCentered(g, label, x + 8, yy + 16, healthColor(dmg));
			}
			i++;
		}
		setSize(18, Math.max(1, i) * 20);
	}

	static int healthColor(float ratio) {
		if (ratio > 0.5f) return 0xFF4CAF50;
		if (ratio > 0.25f) return 0xFFFFC107;
		return 0xFFF44336;
	}
}
