package dev.darkness.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

/** Polls raw GLFW keys each tick (no vanilla KeyMapping registration needed). */
public final class KeyManager {
	private static final boolean[] wasDown = new boolean[512];
	private static final Minecraft mc = Minecraft.getInstance();

	private KeyManager() {
	}

	public static boolean isDown(int key) {
		if (key < 0 || key > 255) return false;
		return InputConstants.isKeyDown(mc.getWindow(), key);
	}

	/** Call once per client tick: fires bind handlers. */
	public static void tick(Runnable onKey, java.util.function.IntConsumer keyConsumer) {
		for (int key = 0; key < 256; key++) {
			boolean down;
			try {
				down = isDown(key);
			} catch (Exception e) {
				continue;
			}
			if (down && !wasDown[key]) {
				keyConsumer.accept(key);
			}
			wasDown[key] = down;
		}
	}

	public static String keyName(int key) {
		if (key < 0) return "—";
		String name = GLFW.glfwGetKeyName(key, 0);
		if (name != null) return name.toUpperCase(Locale.ROOT);
		return GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key)) != null
			? GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key)).toUpperCase(Locale.ROOT)
			: "KEY" + key;
	}
}
