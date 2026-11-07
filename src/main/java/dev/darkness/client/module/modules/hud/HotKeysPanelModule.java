package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.KeyManager;
import dev.darkness.client.util.RenderUtil;
import dev.darkness.client.util.RoundedUI;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/** Панель забинженных клавиш модулей. */
public class HotKeysPanelModule extends HudModule {
	private final int maxRows = 6;

	public HotKeysPanelModule() {
		super("Hot Keys", "Забинженные клавиши модулей");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		List<String[]> rows = new ArrayList<>();
		for (var m : dev.darkness.client.DarknessClient.getModuleManager().getModules()) {
			if (m.getKeybind() > 0 && !(m instanceof HudModule)) {
				rows.add(new String[]{m.getName(), "[" + dev.darkness.client.util.KeyManager.keyName(m.getKeybind()) + "]"});
			}
		}
		int w = 110;
		int shown = Math.min(rows.size(), maxRows);
		int h = 20 + Math.max(1, shown) * RoundedUI.ROW_H;
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		RoundedUI.panel(g, x, y, w, h);
		RoundedUI.header(g, x, y, "Hot Keys", 0xFFB388FF);
		int ry = y + 18;
		if (rows.isEmpty()) {
			RenderUtil.text(g, "§7нет биндов", x + 6, ry + 2, 0xFF757575);
			return;
		}
		int i = 0;
		for (String[] r : rows) {
			if (i >= maxRows) break;
			RoundedUI.row(g, x, ry, w, r[0], r[1], 0xFFE0E0E0, 0xFFB388FF);
			ry += RoundedUI.ROW_H;
			i++;
		}
	}
}
