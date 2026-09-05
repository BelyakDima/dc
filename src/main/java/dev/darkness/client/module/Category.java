package dev.darkness.client.module;

public enum Category {
	COMBAT("Combat", 0xFFE74C3C),
	VISUALS("Visuals", 0xFF9B59B6),
	HUD("HUD", 0xFF3498DB),
	OPTIMIZATION("Optimization", 0xFF2ECC71),
	UTILITY("Utility", 0xFFF1C40F);

	private final String displayName;
	private final int color;

	Category(String displayName, int color) {
		this.displayName = displayName;
		this.color = color;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getColor() {
		return color;
	}
}
