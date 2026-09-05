package dev.darkness.client.module;

import dev.darkness.client.DarknessClient;

public abstract class Setting<T> {
	private final String name;
	private final String description;
	protected T value;

	public Setting(String name, String description, T defaultValue) {
		this.name = name;
		this.description = description;
		this.value = defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
		DarknessClient.getConfigManager().scheduleSave();
	}

	public abstract void setFromString(String raw);

	public String getDisplayValue() {
		return String.valueOf(value);
	}
}
