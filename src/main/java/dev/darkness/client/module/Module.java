package dev.darkness.client.module;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class Module {
	private final String name;
	private final String description;
	private final Category category;
	private final List<Setting<?>> settings = new ArrayList<>();
	private boolean enabled = false;
	private int keybind = -1; // GLFW key code, -1 = unbound
	protected final Minecraft mc = Minecraft.getInstance();

	public Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
	}

	protected void register(Setting<?>... list) {
		for (Setting<?> s : list) settings.add(s);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public List<Setting<?>> getSettings() {
		return settings;
	}

	public Setting<?> getSetting(String n) {
		for (Setting<?> s : settings) {
			if (s.getName().equalsIgnoreCase(n)) return s;
		}
		return null;
	}

	public BooleanSetting bool(String n) {
		Setting<?> s = getSetting(n);
		return s instanceof BooleanSetting b ? b : null;
	}

	public NumberSetting number(String n) {
		Setting<?> s = getSetting(n);
		return s instanceof NumberSetting num ? num : null;
	}

	public ModeSetting mode(String n) {
		Setting<?> s = getSetting(n);
		return s instanceof ModeSetting m ? m : null;
	}

	public ColorSetting color(String n) {
		Setting<?> s = getSetting(n);
		return s instanceof ColorSetting c ? c : null;
	}

	public StringSetting string(String n) {
		Setting<?> s = getSetting(n);
		return s instanceof StringSetting st ? st : null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean state) {
		if (this.enabled == state) return;
		this.enabled = state;
		if (state) {
			onEnable();
		} else {
			onDisable();
		}
		DarknessClient.getConfigManager().scheduleSave();
	}

	public void toggle() {
		setEnabled(!enabled);
		if (mc.player != null) {
			DarknessClient.getNotifications().show(enabled ? getName() + " §aвключён" : getName() + " §cвыключен");
		}
	}

	/** Вызывается при нажатии клавиши модуля. По умолчанию — переключение. */
	public void onKeyPressed() {
		toggle();
	}

	/** Модули-действия (свапы и т.п.): срабатывают как действие, а не переключение,
	 *  и работают даже при открытом экране (инвентарь и т.п.). */
	public boolean isActionOnly() {
		return false;
	}

	public int getKeybind() {
		return keybind;
	}

	public void setKeybind(int keybind) {
		this.keybind = keybind;
		DarknessClient.getConfigManager().scheduleSave();
	}

	protected void onEnable() {
	}

	protected void onDisable() {
	}

	/** Called once per client tick. */
	public void onTick() {
	}

	public String getDisplayInfo() {
		return null;
	}
}
