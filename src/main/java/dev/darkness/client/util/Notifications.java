package dev.darkness.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Notifications {
	public static class Notification {
		public final String text;
		public long createdMs = System.currentTimeMillis();
		public float animation = 0;

		Notification(String text) {
			this.text = text;
		}
	}

	private final List<Notification> list = new ArrayList<>();
	private static final long LIFE_MS = 2500;
	private final Minecraft mc = Minecraft.getInstance();

	public void show(String text) {
		synchronized (list) {
			if (list.size() > 6) list.remove(0);
			list.add(new Notification(text.replaceAll("§.", "")));
		}
	}

	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		synchronized (list) {
			Iterator<Notification> it = list.iterator();
			while (it.hasNext()) {
				Notification n = it.next();
				long age = System.currentTimeMillis() - n.createdMs;
				if (age > LIFE_MS) {
					it.remove();
					continue;
				}
				n.animation = Math.min(1, n.animation + partialTick * 6);
				float slideIn = Math.min(1, age / 200f);
				float fadeOut = age > LIFE_MS - 400 ? Math.max(0, (LIFE_MS - age) / 400f) : 1;
				int alpha = (int) (200 * fadeOut);
				int w = mc.font.width(n.text) + 14;
				int h = 16;
				int x = screenW - (int) (w * slideIn) - 4;
				int y = 8 + list.indexOf(n) * (h + 3);
				RenderUtil.rect(g, x, y, w, h, RenderUtil.withAlpha(0x0B0B10, 0xE0));
				RenderUtil.rect(g, x, y, 2, h, RenderUtil.withAlpha(0x7C4DFF, alpha));
				RenderUtil.text(g, n.text, x + 6, y + 4, 0xE0FFFFFF);
			}
		}
	}
}
