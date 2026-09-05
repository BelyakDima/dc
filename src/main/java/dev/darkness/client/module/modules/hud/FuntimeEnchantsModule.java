package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.module.StringSetting;
import dev.darkness.client.util.FuntimeTracker;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

/** Ловит в чате сервера активации кастомных чар/предметов (трапки, пласты, дезориентации...) */
public class FuntimeEnchantsModule extends HudModule {
	private final StringSetting keywords;
	private final BooleanSetting showAll;

	public FuntimeEnchantsModule() {
		super("FuntimeEnchants", "Видно активации трапок/пластов/дезориентаций из чата");
		keywords = new StringSetting("Ключевые слова", "Через запятую, можно корни слов",
			"трапк, пласт, дезориентац, божь, явн, ступор, замороз, смерч");
		showAll = new BooleanSetting("Все сообщения", "Показывать любые системные сообщения, не только чары", false);
		register(keywords, showAll);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		var events = FuntimeTracker.snapshot();
		if (events.isEmpty()) {
			setSize(120, 12);
			return;
		}
		int w = 200, h = Math.min(events.size(), 6) * 12 + 14;
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		RenderUtil.rect(g, x, y, w, h, 0xC80D0D14);
		RenderUtil.rect(g, x, y, w, 1, 0xFFE040FB);
		RenderUtil.text(g, "Чары/предметы:", x + 4, y + 3, 0xFFE040FB);
		int i = 0;
		long now = System.currentTimeMillis();
		for (var e : events) {
			if (i >= 6) break;
			String text = e.text;
			if (text.length() > 42) text = text.substring(0, 41) + "…";
			long ago = (now - e.time) / 1000;
			String time = ago < 60 ? ago + "с" : (ago / 60) + "м";
			RenderUtil.text(g, text, x + 4, y + 15 + i * 12, 0xFFE0E0E0);
			RenderUtil.textRight(g, time, x + w - 4, y + 15 + i * 12, 0xFF9E9E9E);
			i++;
		}
	}
}
