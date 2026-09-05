package dev.darkness.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.darkness.client.DarknessClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * «Minecraft Customize»: папка config/darkness/customize, куда пользователь кладёт
 * title.png — картинка отображается на главном экране игры (под меню).
 */
public final class CustomizeManager {
	public static final Identifier TITLE_TEXTURE = Identifier.fromNamespaceAndPath("darknessclient", "custom_title");
	private static Path dir;
	private static long lastMtime = -1;
	private static boolean registered = false;
	private static int imgW, imgH;

	private CustomizeManager() {
	}

	public static void init(Path configDir) {
		dir = configDir.resolve("customize");
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			DarknessClient.LOGGER.warn("customize dir", e);
		}
	}

	public static Path getDir() {
		return dir;
	}

	public static void openFolder() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(dir.toFile());
				return;
			}
		} catch (Exception ignored) {
		}
		try {
			Runtime.getRuntime().exec(new String[]{"explorer", dir.toString()});
		} catch (Exception ignored) {
		}
	}

	/** Вызывается из TitleScreenMixin после отрисовки фона, до отрисовки меню. */
	public static void renderTitleBackground(GuiGraphics g, int screenW, int screenH) {
		if (dir == null) return;
		Path file = dir.resolve("title.png");
		if (!Files.exists(file)) return;
		try {
			long mtime = Files.getLastModifiedTime(file).toMillis();
			if (!registered || mtime != lastMtime) {
				NativeImage img = NativeImage.read(Files.newInputStream(file));
				imgW = img.getWidth();
				imgH = img.getHeight();
				Minecraft.getInstance().getTextureManager().register(TITLE_TEXTURE,
					new DynamicTexture(() -> "darkness-title", img));
				registered = true;
				lastMtime = mtime;
			}
			RenderPipeline pipeline = RenderPipelines.GUI;
			g.blit(pipeline, TITLE_TEXTURE, 0, 0, 0.0F, 0.0F, screenW, screenH, imgW, imgH);
		} catch (Exception e) {
			DarknessClient.LOGGER.warn("title.png load failed", e);
		}
	}
}
