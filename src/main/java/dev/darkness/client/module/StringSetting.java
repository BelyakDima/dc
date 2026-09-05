package dev.darkness.client.module;

public class StringSetting extends Setting<String> {
	public StringSetting(String name, String description, String defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public void setFromString(String raw) {
		set(raw);
	}
}
