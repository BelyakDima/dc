package dev.darkness.client.module.modules.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.ChatUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WaypointModule extends Module {
	protected final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();

	public static class Waypoint {
		public double x, y, z;
		public String name;

		public Waypoint(double x, double y, double z, String name) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = name;
		}
	}

	private static final Gson GSON = new Gson();
	private static final List<Waypoint> waypoints = new ArrayList<>();
	private int counter = 1;

	public WaypointModule() {
		super("Waypoints", "Точки: клавиша добавляет текущую позицию, HUD показывает ближайшую", Category.UTILITY);
		setKeybind(GLFW.GLFW_KEY_B);
		load();
	}

	@Override
	public boolean isActionOnly() {
		return true;
	}

	@Override
	public void onKeyPressed() {
		if (!isEnabled()) return;
		if (mc.player == null) return;
		Waypoint wp = new Waypoint(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
			"Точка " + counter++);
		waypoints.add(wp);
		save();
		DarknessClient.getNotifications().show(
			String.format(Locale.ROOT, "§aТочка добавлена: §f%.0f %.0f %.0f", wp.x, wp.y, wp.z));
	}

	@Override
	public void onTick() {
		if (mc.player == null || waypoints.isEmpty()) return;
		Waypoint best = null;
		double bestDist = Double.MAX_VALUE;
		for (Waypoint wp : waypoints) {
			double d = mc.player.distanceToSqr(wp.x, wp.y, wp.z);
			if (d < bestDist) {
				bestDist = d;
				best = wp;
			}
		}
		if (best != null && bestDist < 9) { // ближе 3 блоков — точка достигнута
			waypoints.remove(best);
			save();
			DarknessClient.getNotifications().show("§aТочка достигнута: §f" + best.name);
		}
	}

	public static List<Waypoint> getWaypoints() {
		return waypoints;
	}

	public static void save() {
		try {
			Path p = DarknessClient.getConfigManager().getDir().resolve("waypoints.json");
			JsonArray arr = new JsonArray();
			for (Waypoint wp : waypoints) {
				JsonObject o = new JsonObject();
				o.addProperty("x", wp.x);
				o.addProperty("y", wp.y);
				o.addProperty("z", wp.z);
				o.addProperty("name", wp.name);
				arr.add(o);
			}
			Files.writeString(p, GSON.toJson(arr), StandardCharsets.UTF_8);
		} catch (Exception ignored) {
		}
	}

	private static void load() {
		try {
			Path p = DarknessClient.getConfigManager().getDir().resolve("waypoints.json");
			if (!Files.exists(p)) return;
			JsonArray arr = com.google.gson.JsonParser.parseString(Files.readString(p, StandardCharsets.UTF_8)).getAsJsonArray();
			waypoints.clear();
			for (var e : arr) {
				JsonObject o = e.getAsJsonObject();
				waypoints.add(new Waypoint(o.get("x").getAsDouble(), o.get("y").getAsDouble(),
					o.get("z").getAsDouble(), o.get("name").getAsString()));
			}
		} catch (Exception ignored) {
		}
	}
}
