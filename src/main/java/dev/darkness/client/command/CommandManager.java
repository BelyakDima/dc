package dev.darkness.client.command;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.accounts.Account;
import dev.darkness.client.module.Module;
import dev.darkness.client.ui.AccountsScreen;
import dev.darkness.client.ui.ClickGuiScreen;
import dev.darkness.client.ui.HudEditorScreen;
import dev.darkness.client.util.ChatUtil;
import dev.darkness.client.util.KeyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.Map;

public class CommandManager {
	private static final Map<String, Integer> KEY_NAMES = Map.ofEntries(
		Map.entry("LSHIFT", GLFW.GLFW_KEY_LEFT_SHIFT),
		Map.entry("RSHIFT", GLFW.GLFW_KEY_RIGHT_SHIFT),
		Map.entry("LCTRL", GLFW.GLFW_KEY_LEFT_CONTROL),
		Map.entry("RCTRL", GLFW.GLFW_KEY_RIGHT_CONTROL),
		Map.entry("LALT", GLFW.GLFW_KEY_LEFT_ALT),
		Map.entry("RALT", GLFW.GLFW_KEY_RIGHT_ALT),
		Map.entry("SPACE", GLFW.GLFW_KEY_SPACE),
		Map.entry("ENTER", GLFW.GLFW_KEY_ENTER),
		Map.entry("TAB", GLFW.GLFW_KEY_TAB),
		Map.entry("CAPSLOCK", GLFW.GLFW_KEY_CAPS_LOCK)
	);

	public void dispatch(String line) {
		String[] parts = line.trim().split("\\s+");
		if (parts.length == 0 || parts[0].isEmpty()) return;
		String cmd = parts[0].toLowerCase(Locale.ROOT);
		switch (cmd) {
			case "help" -> {
				ChatUtil.message(".help — список команд");
				ChatUtil.message(".t <модуль> — переключить модуль");
				ChatUtil.message(".bind <модуль> <клавиша|none> — привязка");
				ChatUtil.message(".config save|load|reset — конфиг");
				ChatUtil.message(".accounts list|add|login|remove — аккаунты");
				ChatUtil.message(".gui — меню · .hud — редактор HUD");
				ChatUtil.message("Right Shift — открыть меню");
			}
			case "t", "toggle" -> {
				Module m = needModule(parts);
				if (m != null) m.toggle();
			}
			case "bind" -> {
				if (parts.length < 3) {
					ChatUtil.message("Использование: .bind <модуль> <клавиша|none>");
					return;
				}
				Module m = DarknessClient.getModuleManager().byName(parts[1]);
				if (m == null) {
					ChatUtil.message("§cМодуль не найден: " + parts[1]);
					return;
				}
				String key = parts[2].toUpperCase(Locale.ROOT);
				if (key.equals("NONE")) {
					m.setKeybind(-1);
					ChatUtil.message(m.getName() + ": бинд снят");
					return;
				}
				int code = parseKey(key);
				if (code <= 0) {
					ChatUtil.message("§cНеизвестная клавиша: " + key);
					return;
				}
				m.setKeybind(code);
				ChatUtil.message(m.getName() + " → " + KeyManager.keyName(code));
			}
			case "config" -> {
				if (parts.length < 2) {
					ChatUtil.message("Использование: .config save|load|reset");
					return;
				}
				switch (parts[1].toLowerCase(Locale.ROOT)) {
					case "save" -> {
						DarknessClient.getConfigManager().save();
						ChatUtil.message("§aКонфиг сохранён");
					}
					case "load" -> {
						DarknessClient.getConfigManager().load();
						ChatUtil.message("§aКонфиг загружен");
					}
					case "reset" -> {
						DarknessClient.getConfigManager().reset();
						ChatUtil.message("§aКонфиг сброшен (перезапустите игру)");
					}
					default -> ChatUtil.message("§cНеизвестное действие: " + parts[1]);
				}
			}
			case "accounts" -> accounts(parts);
			case "gui" -> openScreen(() -> new ClickGuiScreen());
			case "hud" -> openScreen(HudEditorScreen::new);
			case "acc" -> openScreen(AccountsScreen::new);
			default -> ChatUtil.message("§cНеизвестная команда: ." + cmd + " (.help)");
		}
	}

	/** ChatScreen закрывается уже после отправки сообщения и затирает setScreen —
	 *  открываем экран отложенно, на следующем проходе главного потока. */
	private void openScreen(java.util.function.Supplier<Screen> supplier) {
		Minecraft mc = Minecraft.getInstance();
		mc.execute(() -> mc.setScreen(supplier.get()));
	}

	private Module needModule(String[] parts) {
		if (parts.length < 2) {
			ChatUtil.message("§cУкажите имя модуля");
			return null;
		}
		Module m = DarknessClient.getModuleManager().byName(parts[1]);
		if (m == null) ChatUtil.message("§cМодуль не найден: " + parts[1]);
		return m;
	}

	private void accounts(String[] parts) {
		var am = DarknessClient.getAccountManager();
		if (parts.length < 2) {
			ChatUtil.message("Использование: .accounts list|add <ник>|login <ник>|remove <ник>");
			return;
		}
		switch (parts[1].toLowerCase(Locale.ROOT)) {
			case "list" -> {
				ChatUtil.message("Аккаунтов: " + am.getAccounts().size());
				for (Account a : am.getAccounts()) {
					ChatUtil.message(" · " + a.getName() + " (" + (a.isOffline() ? "оффлайн" : "лицензия") + ")");
				}
			}
			case "add" -> {
				if (parts.length < 3 || !parts[2].matches("[A-Za-z0-9_]{1,16}")) {
					ChatUtil.message("§cНик: 1-16 символов A-Z, 0-9, _");
					return;
				}
				am.add(Account.offline(parts[2]));
				ChatUtil.message("§aДобавлен: " + parts[2]);
			}
			case "login" -> {
				if (parts.length < 3) {
					ChatUtil.message("§cУкажите ник");
					return;
				}
				for (Account a : am.getAccounts()) {
					if (a.getName().equalsIgnoreCase(parts[2])) {
						if (am.login(a)) ChatUtil.message("§aВход выполнен: " + a.getName());
						return;
					}
				}
				ChatUtil.message("§cАккаунт не найден (.accounts add " + parts[2] + ")");
			}
			case "remove" -> {
				if (parts.length < 3) {
					ChatUtil.message("§cУкажите ник");
					return;
				}
				for (Account a : am.getAccounts()) {
					if (a.getName().equalsIgnoreCase(parts[2])) {
						am.remove(a);
						ChatUtil.message("§aУдалён: " + a.getName());
						return;
					}
				}
				ChatUtil.message("§cАккаунт не найден");
			}
			default -> ChatUtil.message("§cНеизвестное действие");
		}
	}

	private int parseKey(String key) {
		String k = key.toUpperCase(Locale.ROOT);
		if (KEY_NAMES.containsKey(k)) return KEY_NAMES.get(k);
		if (k.length() == 1) {
			char c = k.charAt(0);
			if (c >= 'A' && c <= 'Z') return GLFW.GLFW_KEY_A + (c - 'A');
			if (c >= '0' && c <= '9') return GLFW.GLFW_KEY_0 + (c - '0');
		}
		return -1;
	}
}
