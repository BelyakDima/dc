package dev.darkness.client.util;

import dev.darkness.client.DarknessClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит последние события кастомных чар/предметов Funtime (трапки, пласты,
 * дезориентации и т.д.), найденные в чате сервера.
 */
public final class FuntimeTracker {
	public static class EnchantEvent {
		public final String text;
		public final long time = System.currentTimeMillis();

		EnchantEvent(String text) {
			this.text = text;
		}
	}

	private static final List<EnchantEvent> events = new ArrayList<>();
	private static final int MAX = 6;

	private FuntimeTracker() {
	}

	public static synchronized void processChat(String message) {
		String lower = message.toLowerCase(java.util.Locale.ROOT);
		for (String kw : keywords()) {
			if (lower.contains(kw)) {
				synchronized (events) {
					events.add(0, new EnchantEvent(message.trim()));
					while (events.size() > MAX) events.remove(events.size() - 1);
				}
				return;
			}
		}
	}

	public static synchronized List<EnchantEvent> snapshot() {
		return new ArrayList<>(events);
	}

	public static synchronized void clear() {
		events.clear();
	}

	/** Корни слов, по которым ловим события (словоформы трапка/трапки и т.д.). */
	private static final String[] DEFAULT_KEYWORDS = {
		"трапк", "пласт", "дезориентац", "божь", "явн пыль", "явная пыль",
		"ступор", "замороз", "смерч"
	};

	public static String[] keywords() {
		try {
			var m = DarknessClient.getModuleManager();
			if (m != null) {
				var mod = m.byName("FuntimeEnchants");
				if (mod != null) {
					var s = mod.string("Ключевые слова");
					if (s != null && !s.get().isBlank()) {
						return s.get().toLowerCase(java.util.Locale.ROOT).split("\s*,\s*");
					}
				}
			}
		} catch (Exception ignored) {
		}
		return DEFAULT_KEYWORDS;
	}
}
