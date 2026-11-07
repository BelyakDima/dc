package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.ModeSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * WorldCustomizer: темы неба (Чёрная дыра / Светлое / Тёмные тучи), цвет неба/тумана,
 * скрытие светил. Всё рисуется в screen-space — стоит копейки FPS.
 */
public class WorldCustomizerModule extends Module implements OverlayRenderer {
	public final BooleanSetting customSky;
	public final ColorSetting skyColor;
	public final BooleanSetting customFog;
	public final ColorSetting fogColor;
	public final BooleanSetting hideSunMoon;
	public final ModeSetting skyStyle;
	public final ColorSetting styleColorTop;
	public final ColorSetting styleColorBottom;
	public final BooleanSetting animate;

	public WorldCustomizerModule() {
		super("WorldCustomizer", "Небо: Чёрная дыра / Светлое / Тучи + цвет неба и тумана", Category.VISUALS);
		skyStyle = new ModeSetting("Тема неба", "Чёрная дыра / Светлое / Тучи",
			java.util.List.of("Стандартное", "Чёрная дыра", "Светлое", "Тучи"), "Стандартное");
		customSky = new BooleanSetting("Свой цвет неба", "Заменить цвет неба", true);
		skyColor = new ColorSetting("Цвет неба", "Цвет неба (для Стандартного)", 0xFF1B2A4A);
		customFog = new BooleanSetting("Свой цвет тумана", "Заменить цвет тумана", false);
		fogColor = new ColorSetting("Цвет тумана", "Цвет тумана", 0xFF22334F);
		hideSunMoon = new BooleanSetting("Скрыть солнце/луну/звёзды", "Убрать светила с неба", false);
		styleColorTop = new ColorSetting("Палитра: верх", "Верхний цвет темы", 0xFF101018);
		styleColorBottom = new ColorSetting("Палитра: низ", "Нижний цвет темы", 0xFF18324A);
		animate = new BooleanSetting("Анимация", "Живое движение неба", true);
		register(skyStyle, customSky, skyColor, customFog, fogColor, hideSunMoon, styleColorTop, styleColorBottom, animate);
	}

	private int starSeed = 42;

	private boolean isStyled() {
		return isEnabled() && !skyStyle.is("Стандартное");
	}

	@Override
	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (!isStyled() || mc.player == null) return;
		if (mc.options.hideGui) return;

		// горизонт: точка далеко впереди на уровне глаз
		Vec3 look = mc.player.getViewVector(1.0f);
		double hx = mc.player.getX() + look.x * 400;
		double hy = mc.player.getEyeY();
		double hz = mc.player.getZ() + look.z * 400;
		float[] h = RenderUtil.worldToScreen(hx, hy, hz);
		int horizonY = h != null ? (int) h[1] : screenH / 2;
		if (horizonY > screenH) horizonY = screenH;
		if (horizonY < -50) horizonY = -50;

		long time = System.currentTimeMillis();
		float anim = animate.get() ? (time % 60000L) / 1000f : 0f;

		switch (skyStyle.get()) {
			case "Чёрная дыра" -> drawBlackHole(g, screenW, screenH, horizonY, anim);
			case "Светлое" -> drawLight(g, screenW, horizonY, anim);
			case "Тучи" -> drawClouds(g, screenW, screenH, horizonY, anim);
		}
	}

	/** Вертикальный градиент от top (небо) к bottom (горизонт). */
	private void drawGradientSky(GuiGraphics g, int screenW, int horizonY, int top, int bottom) {
		if (horizonY <= 0) return;
		int steps = 36;
		int th = Math.max(1, horizonY);
		int tr = (top >> 16) & 0xFF, tg = (top >> 8) & 0xFF, tb = top & 0xFF;
		int br = (bottom >> 16) & 0xFF, bg = (bottom >> 8) & 0xFF, bb = bottom & 0xFF;
		for (int i = 0; i < steps; i++) {
			float t = i / (float) (steps - 1);
			int y0 = (int) (t * th);
			int y1 = (int) ((i + 1) / (float) steps * th);
			int r = (int) (tr + (br - tr) * t);
			int gg = (int) (tg + (bg - tg) * t);
			int b = (int) (tb + (bb - tb) * t);
			RenderUtil.rect(g, 0, y0, screenW, y1 - y0 + 1, 0xFF000000 | (r << 16) | (gg << 8) | b);
		}
	}

	/** Чёрная дыра: чёрное небо + звёзды + аккреционный диск. */
	private void drawBlackHole(GuiGraphics g, int screenW, int screenH, int horizonY, float anim) {
		// тёмно-фиолетовое небо (верх чёрный)
		drawGradientSky(g, screenW, horizonY, 0xFF020208, 0xFF141028);

		// звёзды (детерминированные по seed)
		Random rnd = new Random(starSeed);
		for (int i = 0; i < 90; i++) {
			float fx = rnd.nextFloat();
			float fy = rnd.nextFloat();
			int sx = (int) (fx * screenW);
			int sy = (int) (fy * Math.max(1, horizonY - 10));
			int size = rnd.nextBoolean() ? 1 : 2;
			int tw = (int) (150 + 105 * Math.abs(Math.sin(time() * 0.002 + i)));
			RenderUtil.rect(g, sx, sy, size, size, (tw << 24) | 0xFFFFFF);
		}

		// центр дыры: чуть выше центра экрана, горизонтальный эллипс-диск
		int cx = screenW / 2;
		int cy = Math.max(60, horizonY / 2 - 30);
		float rot = (float) Math.sin(time() * 0.0006) * 6f; // лёгкое покачивание
		int rx = Math.min(240, screenW / 5), ry = rx / 4;

		// оранжевое свечение справа
		for (int i = 6; i > 0; i--) {
			int a = 22 * (7 - i);
			RenderUtil.rect(g, cx + rx / 2 - 20 + i * 3, cy - ry / 2, i * 10 + 30, ry, (a << 24) | 0xFF8A2BE2 | 0);
		}

		// внешнее кольцо: белое с оранжевым правым краем
		int rings = 46;
		for (int i = 0; i < rings; i++) {
			double ang = i / (float) rings * 2 * Math.PI;
			double ex = Math.cos(ang) * (rx + 8 + rot);
			double ey = Math.sin(ang) * (ry + 3 + rot * 0.2);
			// правый край оранжевый, остальное белое
			boolean right = Math.cos(ang) > 0.15;
			int col = right ? 0xFFFFB86C : 0xFFFFFFFF;
			int px = cx + (int) ex;
			int py = cy - (int) (ey * 0.9); // сплюснутый эллипс (диск почти с ребра)
			int size = 1 + (int) (2 * (1 - Math.abs(ang / Math.PI - 0.5) * 2 * 0.3));
			RenderUtil.rect(g, px, py, size, size, col);
			if (i % 2 == 0) {
				RenderUtil.rect(g, px, py + (int) (ry * 0.55), size, size, (120 << 24) | 0x9FB8FF);
			}
		}

		// само отверстие (чёрный эллипс) поверх
		fillEllipse(g, cx, cy, rx - 6, ry - 2, 0xFF000000);
		// нижняя дуга (белая подсветка снизу)
		for (int i = 0; i < 26; i++) {
			double ang = Math.PI + i / 25.0 * Math.PI;
			double ex = Math.cos(ang) * (rx - 10);
			double ey = Math.sin(ang) * (ry - 2);
			int px = cx + (int) ex;
			int py = cy - (int) ey;
			RenderUtil.rect(g, px, py, 2, 2, 0xFFFFFFFF);
		}
	}

	/** Светлое небо: пастельный бело-циановый градиент с зелёной дымкой у горизонта. */
	private void drawLight(GuiGraphics g, int screenW, int horizonY, float anim) {
		int top = styleColorTop.get();
		int bottom = styleColorBottom.get();
		// по фото: белый сверху → циан → зелёный у горизонта
		int mid = RenderUtil.lerpColor(top, bottom, 0.55f);
		int greenBottom = RenderUtil.lerpColor(bottom, 0xFF1FBF62, 0.65f);
		if (animate.get()) {
			float shift = (float) Math.sin(time() * 0.0004) * 0.08f;
			top = RenderUtil.lerpColor(top, 0xFFFFFFFF, 0.15f + shift);
		}
		drawGradientSky(g, screenW, horizonY / 2, top, mid);
		drawGradientSky(g, screenW, horizonY, mid, greenBottom);
		// мягкое белое пятно (свечение)
		softEllipse(g, screenW / 4, horizonY / 3, screenW / 3, horizonY / 4, 0x66FFFFFF, anim);
	}

	/** Тёмные тучи: тёмно-серые силуэты облаков. */
	private void drawClouds(GuiGraphics g, int screenW, int screenH, int horizonY, float anim) {
		drawGradientSky(g, screenW, horizonY, 0xFF0B0B10, 0xFF1A1C22);
		Random rnd = new Random(7);
		int clouds = 7;
		for (int i = 0; i < clouds; i++) {
			float fx = rnd.nextFloat();
			float fy = 0.15f + rnd.nextFloat() * 0.6f;
			int cx = (int) ((fx + anim * 0.004f * (i % 2 == 0 ? 1 : -1)) * screenW) % (screenW + 200) - 100;
			int cy = (int) (fy * horizonY);
			int cw = screenW / 4 + rnd.nextInt(screenW / 5);
			int ch = horizonY / 5 + rnd.nextInt(10);
			softEllipse(g, cx, cy, cw / 2, ch / 2, 0x552A2A30, anim);
			softEllipse(g, cx + cw / 5, cy - ch / 4, cw / 3, ch / 3, 0x33222B44, anim);
		}
	}

	private long time() {
		return System.currentTimeMillis();
	}

	/** Заливка эллипса горизонтальными полосами (дёшево). */
	private void fillEllipse(GuiGraphics g, int cx, int cy, int rx, int ry, int argb) {
		int rows = Math.max(6, ry);
		for (int i = -rows; i <= rows; i++) {
			float t = i / (float) rows;
			int half = (int) (rx * Math.sqrt(Math.max(0, 1 - t * t)));
			if (half <= 0) continue;
			RenderUtil.rect(g, cx - half, cy + i, half * 2, 1, argb);
		}
	}

	/** Мягкий (полупрозрачный, размытый) эллипс из нескольких слоёв. */
	private void softEllipse(GuiGraphics g, int cx, int cy, int rx, int ry, int argb, float anim) {
		int a = (argb >>> 24) & 0xFF;
		int rgb = argb & 0xFFFFFF;
		int layers = 4;
		for (int i = layers; i >= 1; i--) {
			float k = 1f + i * 0.35f;
			int la = (int) (a / (i * 1.6f));
			if (la <= 2) continue;
			fillEllipse(g, cx, cy, (int) (rx * k), (int) (ry * k), (la << 24) | rgb);
		}
	}
}
