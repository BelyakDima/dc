package dev.darkness.client.module;

import java.util.List;

public class ModeSetting extends Setting<String> {
	private final List<String> modes;

	public ModeSetting(String name, String description, List<String> modes, String defaultMode) {
		super(name, description, defaultMode);
		this.modes = modes;
	}

	public List<String> getModes() {
		return modes;
	}

	public void cycle() {
		int idx = modes.indexOf(value);
		set(modes.get((idx + 1) % modes.size()));
	}

	public boolean is(String mode) {
		return value.equalsIgnoreCase(mode);
	}

	@Override
	public void setFromString(String raw) {
		for (String mode : modes) {
			if (mode.equalsIgnoreCase(raw)) {
				set(mode);
				return;
			}
		}
	}

	@Override
	public void set(String value) {
		if (modes.contains(value)) {
			super.set(value);
		}
	}
}
