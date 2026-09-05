package dev.darkness.client.module;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberSetting extends Setting<Double> {
	private final double min;
	private final double max;
	private final double step;
	private final int decimals;

	public NumberSetting(String name, String description, double defaultValue, double min, double max, double step) {
		super(name, description, defaultValue);
		this.min = min;
		this.max = max;
		this.step = step;
		String s = String.valueOf(step);
		this.decimals = s.contains(".") ? s.length() - s.indexOf('.') - 1 : 0;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getStep() {
		return step;
	}

	public int getDecimals() {
		return decimals;
	}

	@Override
	public void set(Double value) {
		double v = Math.max(min, Math.min(max, value));
		if (step > 0) {
			v = Math.round((v - min) / step) * step + min;
		}
		BigDecimal bd = new BigDecimal(v).setScale(decimals, RoundingMode.HALF_UP);
		super.set(bd.doubleValue());
	}

	@Override
	public void setFromString(String raw) {
		try {
			set(Double.parseDouble(raw));
		} catch (NumberFormatException ignored) {
		}
	}

	@Override
	public String getDisplayValue() {
		return String.format("%." + decimals + "f", value);
	}
}
