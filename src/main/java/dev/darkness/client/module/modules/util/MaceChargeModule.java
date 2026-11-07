package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.MaceTextureTinter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

public class MaceChargeModule extends Module {
	private final BooleanSetting enabled2;
	private final ColorSetting chargedColor;
	private final ColorSetting notChargedColor;
	private int prevState = -1; // 0=не мейс, 1=заряжен, 2=не заряжен

	public MaceChargeModule() {
		super("MaceCharge", "Мейс в руке зеленеет при полном заряде, краснеет при незаряженном", Category.VISUALS);
		enabled2 = new BooleanSetting("Работать", "Перекраска текстуры мейса", true);
		chargedColor = new ColorSetting("Цвет заряженного", "Полный заряд", 0xFF00FF00);
		notChargedColor = new ColorSetting("Цвет незаряженного", "Не хватает высоты", 0xFFFF2222);
		register(enabled2, chargedColor, notChargedColor);
	}

	@Override
	public void onTick() {
		int state;
		if (!enabled2.get() || mc.player == null || !(mc.player.getMainHandItem().getItem() instanceof MaceItem)) {
			state = 0;
		} else {
			// полный заряд мейса: высота падения больше порога смэш-атаки (1.5 блока)
			state = mc.player.fallDistance > 1.5f ? 1 : 2;
		}
		if (state == prevState) return;
		prevState = state;

		switch (state) {
			case 1 -> MaceTextureTinter.recolor(chargedColor.get());
			case 2 -> MaceTextureTinter.recolor(notChargedColor.get());
			default -> MaceTextureTinter.restore();
		}
	}

	@Override
	protected void onDisable() {
		MaceTextureTinter.restore();
		prevState = -1;
	}
}
