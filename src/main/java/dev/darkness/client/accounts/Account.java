package dev.darkness.client.accounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Account {
	private final String name;
	private final UUID uuid;
	private final String type; // "offline" | "microsoft"

	public Account(String name, UUID uuid, String type) {
		this.name = name;
		this.uuid = uuid;
		this.type = type;
	}

	public static Account offline(String name) {
		return new Account(name, offlineUuid(name), "offline");
	}

	public static UUID offlineUuid(String name) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}

	public String getName() {
		return name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getType() {
		return type;
	}

	public boolean isOffline() {
		return "offline".equals(type);
	}
}
