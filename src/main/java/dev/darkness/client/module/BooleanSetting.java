package dev.darkness.client.module;

public class BooleanSetting extends Setting<Boolean> {
	public BooleanSetting(String name, String description, boolean defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public void setFromString(String raw) {
		set(raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("on") || raw.equalsIgnoreCase("1"));
	}
}
