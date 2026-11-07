package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import dev.darkness.client.util.RoundedUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Панель кулдаунов важных предметов (жемчуг, вспышка и т.п.). */
public class CooldownsPanelModule extends HudModule {
	public CooldownsPanelModule() {
		super("Cooldowns", "Кулдауны предметов (жемчуг и др.)");
	}

	private record Entry(Item item, String name) {
	}

	private static final List<Entry> TRACKED = List.of(
		new Entry(Items.ENDER_PEARL, "Ender Pearl"),
		new Entry(Items.WIND_CHARGE, "Wind Charge"),
		new Entry(Items.CHORUS_FRUIT, "Chorus"),
		new Entry(Items.GOAT_HORN, "Goat Horn"),
		new Entry(Items.SHIELD, "Shield")
	);

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) {
			setSize(120, 36);
			return;
		}
		var cd = mc.player.getCooldowns();
		List<String[]> rows = new ArrayList<>();
		int w = 120;
		for (Entry e : TRACKED) {
			ItemStack probe = new ItemStack(e.item());
			if (cd.isOnCooldown(probe)) {
				float pct = cd.getCooldownPercent(probe, partialTick);
				int total = guessTotal(e.item());
				int sec = Math.max(1, Math.round(pct * total));
				rows.add(new String[]{e.name(), String.format("0:%02d", sec)});
			}
		}
		int h = 20 + rows.size() * RoundedUI.ROW_H;
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		RoundedUI.panel(g, x, y, w, h);
		RoundedUI.header(g, x, y, "Cooldowns", 0xFFB388FF);
		int ry = y + 18;
		if (rows.isEmpty()) {
			RenderUtil.text(g, "§7нет кулдаунов", x + 6, ry + 2, 0xFF757575);
			return;
		}
		for (String[] r : rows) {
			RoundedUI.row(g, x, ry, w, r[0], r[1], 0xFFE0E0E0, 0xFF66BB6A);
			ry += RoundedUI.ROW_H;
		}
	}

	private static int guessTotal(Item item) {
		if (item == Items.ENDER_PEARL) return 15; // тайминги для пвп-серверов
		if (item == Items.WIND_CHARGE) return 3;
		if (item == Items.CHORUS_FRUIT) return 5;
		return 20;
	}
}
