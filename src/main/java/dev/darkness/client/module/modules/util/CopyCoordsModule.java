package dev.darkness.client.module.modules.util;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.ChatUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class CopyCoordsModule extends Module {
	public CopyCoordsModule() {
		super("CopyCoords", "Копирует координаты в буфер обмена по клавише (по умолчанию O)", Category.UTILITY);
		setKeybind(GLFW.GLFW_KEY_O);
	}

	@Override
	public boolean isActionOnly() {
		return true;
	}

	@Override
	public void onKeyPressed() {
		if (!isEnabled() || mc.player == null || mc.keyboardHandler == null) return;
		String coords = String.format(Locale.ROOT, "%.0f %.0f %.0f",
			mc.player.getX(), mc.player.getY(), mc.player.getZ());
		mc.keyboardHandler.setClipboard(coords);
		DarknessClient.getNotifications().show("§aСкопировано: §f" + coords);
	}
}
