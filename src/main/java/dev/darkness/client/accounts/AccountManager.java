package dev.darkness.client.accounts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.darkness.client.DarknessClient;
import dev.darkness.client.mixin.MinecraftAccessor;
import dev.darkness.client.util.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final List<Account> accounts = new ArrayList<>();
	private final Path file;
	private final Minecraft mc = Minecraft.getInstance();

	public AccountManager(Path configDir) {
		this.file = configDir.resolve("accounts.json");
		load();
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void add(Account account) {
		accounts.removeIf(a -> a.getName().equalsIgnoreCase(account.getName()));
		accounts.add(account);
		save();
	}

	public void remove(Account account) {
		accounts.remove(account);
		save();
	}

	public boolean login(Account account) {
		try {
			User user = new User(account.getName(), account.getUuid(), "0", Optional.empty(), Optional.empty());
			((MinecraftAccessor) mc).darkness$setUser(user);
			DarknessClient.getNotifications().show("Аккаунт: §a" + account.getName());
			DarknessClient.getConfigManager().scheduleSave();
			return true;
		} catch (Exception e) {
			DarknessClient.LOGGER.warn("Account login failed", e);
			DarknessClient.getNotifications().show("§cОшибка входа: " + e.getMessage());
			return false;
		}
	}

	public String getCurrentName() {
		return ((MinecraftAccessor) mc).darkness$getUser().getName();
	}

	private void load() {
		if (!Files.exists(file)) return;
		try {
			JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
			accounts.clear();
			if (root.has("accounts")) {
				for (var e : root.getAsJsonArray("accounts")) {
					JsonObject o = e.getAsJsonObject();
					accounts.add(new Account(
						o.get("name").getAsString(),
						UUID.fromString(o.get("uuid").getAsString()),
						o.has("type") ? o.get("type").getAsString() : "offline"));
				}
			}
		} catch (Exception ex) {
			DarknessClient.LOGGER.warn("Accounts load failed", ex);
		}
	}

	private void save() {
		try {
			Files.createDirectories(file.getParent());
			JsonObject root = new JsonObject();
			JsonArray arr = new JsonArray();
			for (Account a : accounts) {
				JsonObject o = new JsonObject();
				o.addProperty("name", a.getName());
				o.addProperty("uuid", a.getUuid().toString());
				o.addProperty("type", a.getType());
				arr.add(o);
			}
			root.add("accounts", arr);
			Files.writeString(file, GSON.toJson(root), StandardCharsets.UTF_8);
		} catch (IOException e) {
			DarknessClient.LOGGER.warn("Accounts save failed", e);
		}
	}
}
