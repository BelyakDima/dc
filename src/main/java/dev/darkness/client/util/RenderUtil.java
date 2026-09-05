package dev.darkness.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * World-to-screen projection + 2D drawing helpers.
 * Projection is rebuilt lazily each frame from the camera state, mirroring
 * GameRenderer#renderLevel (view = rotation conjugate, proj = getProjectionMatrix(getFov)).
 */
public final class RenderUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	private static Matrix4f viewProj = new Matrix4f();
	private static boolean valid = false;

	private RenderUtil() {
	}

	public static void updateProjection(float partialTick) {
		if (mc.level == null || mc.player == null) {
			valid = false;
			return;
		}
		try {
			var camera = mc.gameRenderer.getMainCamera();
			if (!camera.isInitialized()) {
				valid = false;
				return;
			}
			float fov = (float) mc.options.fov().get().doubleValue();
			Matrix4f proj = mc.gameRenderer.getProjectionMatrix(fov);
			Quaternionf rotInv = camera.rotation().conjugate(new Quaternionf());
			Matrix4f view = new Matrix4f().rotation(rotInv);
			var camPos = camera.position();
			view.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);
			viewProj = new Matrix4f(proj).mul(view);
			valid = true;
		} catch (Exception e) {
			valid = false;
		}
	}

	/** @return {x, y} in gui-scaled screen coordinates, or null if behind camera. */
	public static float[] worldToScreen(double x, double y, double z) {
		if (!valid) return null;
		Vector3d v = new Vector3d(x, y, z);
		Vector3f clip = new Vector3f();
		// manual transform: clip = viewProj * (x,y,z,1)
		float cx = (float) x, cy = (float) y, cz = (float) z;
		float w = viewProj.m30() * cx + viewProj.m31() * cy + viewProj.m32() * cz + viewProj.m33();
		if (w <= 0.001f) return null;
		float nx = viewProj.m00() * cx + viewProj.m01() * cy + viewProj.m02() * cz + viewProj.m03();
		float ny = viewProj.m10() * cx + viewProj.m11() * cy + viewProj.m12() * cz + viewProj.m13();
		nx /= w;
		ny /= w;
		int sw = mc.getWindow().getGuiScaledWidth();
		int sh = mc.getWindow().getGuiScaledHeight();
		float sx = (nx * 0.5f + 0.5f) * sw;
		float sy = (1.0f - (ny * 0.5f + 0.5f)) * sh;
		if (sx < -2000 || sx > sw + 2000 || sy < -2000 || sy > sh + 2000) return null;
		return new float[]{sx, sy};
	}

	public static boolean projectionValid() {
		return valid;
	}

	// ---- 2D helpers ----

	public static void rect(GuiGraphics g, int x, int y, int w, int h, int argb) {
		g.fill(x, y, x + w, y + h, argb);
	}

	public static void outline(GuiGraphics g, int x, int y, int w, int h, int argb) {
		g.fill(x, y, x + w, y + 1, argb);
		g.fill(x, y + h - 1, x + w, y + h, argb);
		g.fill(x, y, x + 1, y + h, argb);
		g.fill(x + w - 1, y, x + w, y + h, argb);
	}

	public static void hLine(GuiGraphics g, float x1, float x2, float y, int argb) {
		if (x2 < x1) {
			float t = x1;
			x1 = x2;
			x2 = t;
		}
		g.fill((int) Math.floor(x1), (int) y, (int) Math.ceil(x2), (int) y + 1, argb);
	}

	public static void line(GuiGraphics g, float x1, float y1, float x2, float y2, int argb) {
		float dx = x2 - x1, dy = y2 - y1;
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		if (dist < 0.01f) return;
		int steps = (int) Math.ceil(dist); // one pixel per step
		float xi = dx / steps, yi = dy / steps;
		float x = x1, y = y1;
		for (int i = 0; i <= steps; i++) {
			g.fill((int) x, (int) y, (int) x + 1, (int) y + 1, argb);
			x += xi;
			y += yi;
		}
	}

	public static int text(GuiGraphics g, String text, int x, int y, int argb) {
		g.drawString(mc.font, text, x, y, argb, true);
		return mc.font.width(text);
	}

	public static int textCentered(GuiGraphics g, String text, int cx, int y, int argb) {
		int w = mc.font.width(text);
		g.drawString(mc.font, text, cx - w / 2, y, argb, true);
		return w;
	}

	public static int textRight(GuiGraphics g, String text, int rightX, int y, int argb) {
		int w = mc.font.width(text);
		g.drawString(mc.font, text, rightX - w, y, argb, true);
		return w;
	}

	public static int withAlpha(int rgb, int alpha) {
		return (alpha << 24) | (rgb & 0xFFFFFF);
	}

	public static int lerpColor(int a, int b, float t) {
		int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
		int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
		int r = (int) (ar + (br - ar) * t);
		int gg = (int) (ag + (bg - ag) * t);
		int bl = (int) (ab + (bb - ab) * t);
		return 0xFF000000 | (r << 16) | (gg << 8) | bl;
	}
}
