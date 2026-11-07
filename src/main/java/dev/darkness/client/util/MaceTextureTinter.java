package dev.darkness.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import dev.darkness.client.DarknessClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.InputStream;

/**
 * Перекраска текстуры мейса на клиенте: заряжен — зелёный, не заряжен — красный.
 * Текстура мейса заменяется динамической и перекрашивается при смене состояния,
 * поэтому предмет в руке выглядит по-другому физически (как текстура).
 */
public final class MaceTextureTinter {
	public static final Identifier MACE_TEXTURE =
		Identifier.fromNamespaceAndPath("minecraft", "textures/item/mace.png");

	private static NativeImage original;
	private static DynamicTexture dynamic;
	private static int lastColor = -1;
	private static boolean registered = false;

	private MaceTextureTinter() {
	}

	/** Перекрасить текстуру мейса в argb (rgb-канал, альфа сохраняется). */
	public static synchronized void recolor(int argb) {
		Minecraft mc = Minecraft.getInstance();
		if (original == null) {
			try {
				var stack = mc.getResourceManager().getResourceStack(MACE_TEXTURE);
				if (stack.isEmpty()) return;
				InputStream in = stack.get(0).open();
				original = NativeImage.read(in);
			} catch (Exception e) {
				DarknessClient.LOGGER.warn("mace texture read failed", e);
				return;
			}
		}
		if (argb == lastColor) return;
		lastColor = argb;

		int w = original.getWidth(), h = original.getHeight();
		NativeImage tinted = new NativeImage(original.format(), w, h, false);
		int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int px = original.getPixel(x, y);
				int a = (px >> 24) & 0xFF;
				if (a == 0) continue;
				// setPixel ожидает ABGR-порядок: красный должен оказаться в младшем байте
				tinted.setPixelABGR(x, y, (a << 24) | (b << 16) | (g << 8) | r);
			}
		}
		if (dynamic == null) {
			dynamic = new DynamicTexture(() -> "darkness-mace", tinted);
			mc.getTextureManager().register(MACE_TEXTURE, dynamic);
			registered = true;
		} else {
			dynamic.setPixels(tinted);
			dynamic.upload();
		}
	}

	/** Вернуть оригинальную текстуру. */
	public static synchronized void restore() {
		if (dynamic != null && original != null) {
			NativeImage copy = new NativeImage(original.format(), original.getWidth(), original.getHeight(), false);
			copy.copyFrom(original);
			dynamic.setPixels(copy);
			dynamic.upload();
		}
		lastColor = -1;
	}

	public static boolean isRegistered() {
		return registered;
	}
}
