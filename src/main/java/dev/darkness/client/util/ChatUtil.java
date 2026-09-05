package dev.darkness.client.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public final class ChatUtil {
	private static final Minecraft mc = Minecraft.getInstance();

	private ChatUtil() {
	}

	public static void message(String text) {
		if (mc.player == null) return;
		mc.player.displayClientMessage(
			Component.literal(ChatFormatting.DARK_GRAY + "[" + ChatFormatting.DARK_PURPLE + "Darkness" + ChatFormatting.DARK_GRAY + "] " + ChatFormatting.GRAY + text),
			false);
	}

	public static void raw(String text) {
		if (mc.player == null) return;
		mc.player.displayClientMessage(Component.literal(text), false);
	}
}
