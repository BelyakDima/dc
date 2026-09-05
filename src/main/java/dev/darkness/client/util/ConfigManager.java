package dev.darkness.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.Setting;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final Path dir;
	private final Path configFile;
	private long lastSaveRequest = 0;
	private boolean dirty = false;

	public ConfigManager() {
		Path configRoot = Minecraft.getInstance().gameDirectory.toPath().resolve("config");
		this.dir = configRoot.resolve("darkness");
		this.configFile = dir.resolve("config.json");
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			DarknessClient.LOGGER.warn("Could not create config dir", e);
		}
	}

	public Path getDir() {
		return dir;
	}

	public void scheduleSave() {
		dirty = true;
		lastSaveRequest = System.currentTimeMillis();
	}

	/** Saves if a save was requested and 2s passed without further changes. */
	public void tick() {
		if (dirty && System.currentTimeMillis() - lastSaveRequest > 2000) {
			save();
		}
	}

	public synchronized void save() {
		JsonObject root = new JsonObject();
		JsonArray modules = new JsonArray();
		for (Module m : DarknessClient.getModuleManager().getModules()) {
			JsonObject mo = new JsonObject();
			mo.addProperty("name", m.getName());
			mo.addProperty("enabled", m.isEnabled());
			mo.addProperty("key", m.getKeybind());
			if (m instanceof dev.darkness.client.module.HudModule hud) {
				mo.addProperty("x", hud.getRelX());
				mo.addProperty("y", hud.getRelY());
			}
			JsonObject so = new JsonObject();
			for (Setting<?> s : m.getSettings()) {
				if (s.get() instanceof Integer) {
					so.addProperty(s.getName(), (Integer) s.get());
				} else if (s.get() instanceof Double) {
					so.addProperty(s.getName(), (Double) s.get());
				} else if (s.get() instanceof Boolean) {
					so.addProperty(s.getName(), (Boolean) s.get());
				} else {
					so.addProperty(s.getName(), String.valueOf(s.get()));
				}
			}
			mo.add("settings", so);
			modules.add(mo);
		}
		root.add("modules", modules);
		try {
			Files.writeString(configFile, GSON.toJson(root), StandardCharsets.UTF_8);
			dirty = false;
		} catch (IOException e) {
			DarknessClient.LOGGER.warn("Config save failed", e);
		}
	}

	public synchronized void load() {
		if (!Files.exists(configFile)) return;
		try {
			JsonObject root = JsonParser.parseString(Files.readString(configFile, StandardCharsets.UTF_8)).getAsJsonObject();
			if (!root.has("modules")) return;
			for (var me : root.getAsJsonArray("modules")) {
				JsonObject mo = me.getAsJsonObject();
				Module m = DarknessClient.getModuleManager().byName(mo.get("name").getAsString());
				if (m == null) continue;
				if (mo.has("key")) m.setKeybind(mo.get("key").getAsInt());
				if (mo.has("settings")) {
					for (var se : mo.getAsJsonObject("settings").entrySet()) {
						Setting<?> s = m.getSetting(se.getKey());
						if (s == null) continue;
						var v = se.getValue();
						if (s instanceof dev.darkness.client.module.NumberSetting && v.isJsonPrimitive()) {
							((dev.darkness.client.module.NumberSetting) s).setFromString(v.getAsString());
						} else if (s instanceof dev.darkness.client.module.BooleanSetting && v.isJsonPrimitive()) {
							((dev.darkness.client.module.BooleanSetting) s).setFromString(v.getAsString());
						} else if (s instanceof dev.darkness.client.module.ModeSetting && v.isJsonPrimitive()) {
							((dev.darkness.client.module.ModeSetting) s).setFromString(v.getAsString());
						} else if (s instanceof dev.darkness.client.module.ColorSetting && v.isJsonPrimitive()) {
							((dev.darkness.client.module.ColorSetting) s).setFromString(v.getAsString());
						} else if (s instanceof dev.darkness.client.module.StringSetting && v.isJsonPrimitive()) {
							((dev.darkness.client.module.StringSetting) s).setFromString(v.getAsString());
						}
					}
				}
				if (m instanceof dev.darkness.client.module.HudModule hud) {
					if (mo.has("x")) hud.setPosition(mo.get("x").getAsDouble(), hud.getRelY());
					if (mo.has("y")) hud.setPosition(hud.getRelX(), mo.get("y").getAsDouble());
				}
				if (mo.has("enabled")) {
					m.setEnabled(mo.get("enabled").getAsBoolean());
				}
			}
		} catch (Exception e) {
			DarknessClient.LOGGER.warn("Config load failed", e);
		}
	}

	public void reset() {
		try {
			Files.deleteIfExists(configFile);
		} catch (IOException ignored) {
		}
	}
}
