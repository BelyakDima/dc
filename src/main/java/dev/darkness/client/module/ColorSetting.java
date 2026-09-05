package dev.darkness.client.module;

public class ColorSetting extends Setting<Integer> {

	public ColorSetting(String name, String description, int defaultArgb) {
		super(name, description, defaultArgb);
	}

	public int alpha() {
		return (value >>> 24) & 0xFF;
	}

	public int red() {
		return (value >>> 16) & 0xFF;
	}

	public int green() {
		return (value >>> 8) & 0xFF;
	}

	public int blue() {
		return value & 0xFF;
	}

	public int rgb() {
		return 0xFF000000 | (value & 0xFFFFFF);
	}

	public void shiftHue(float degrees) {
		float[] hsb = java.awt.Color.RGBtoHSB(red(), green(), blue(), null);
		float hue = (hsb[0] + degrees / 360.0f) % 1.0f;
		if (hue < 0) hue += 1.0f;
		int rgb = java.awt.Color.HSBtoRGB(hue, hsb[1], hsb[2]);
		set((alpha() << 24) | (rgb & 0xFFFFFF));
	}

	@Override
	public void setFromString(String raw) {
		try {
			String s = raw.startsWith("#") ? raw.substring(1) : raw;
			if (s.matches("-?\\d+")) {
				// signed decimal (как хранится в конфиге)
				set((int) Long.parseLong(s));
			} else {
				set((int) (0xFF000000L | Long.parseLong(s, 16)));
			}
		} catch (NumberFormatException ignored) {
		}
	}

	@Override
	public String getDisplayValue() {
		return String.format("#%06X", value & 0xFFFFFF);
	}
}
