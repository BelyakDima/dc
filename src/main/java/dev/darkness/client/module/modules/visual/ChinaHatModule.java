package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** China Hat — конус зоны удара перед игроком. */
public class ChinaHatModule extends Module implements OverlayRenderer {
	private final ColorSetting color;
	private final NumberSetting radius;
	private final NumberSetting angle;
	private final BooleanSetting fill;

	public ChinaHatModule() {
		super("ChinaHat", "Зона поражения удара перед вами", Category.VISUALS);
		color = new ColorSetting("Цвет", "Цвет конуса", 0xFFFF4444);
		radius = new NumberSetting("Радиус", "Дальность удара (блоки)", 3, 1, 6, 0.5);
		angle = new NumberSetting("Угол", "Ширина конуса (градусы)", 70, 20, 160, 10);
		fill = new BooleanSetting("Заливка", "Полупрозрачная заливка", true);
		register(color, radius, angle, fill);
	}

	@Override
	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) return;
		Vec3 center = mc.player.position();
		float yaw = mc.player.getYRot();

		int segs = 16;
		double spread = Math.toRadians(angle.get());
		double r = radius.get();
		int argb = RenderUtil.withAlpha(color.rgb(), 90);
		int line = RenderUtil.withAlpha(color.rgb(), 200);

		Vec3 prev = null;
		List<float[]> arcScreen = new ArrayList<>();
		for (int i = 0; i <= segs; i++) {
			double t = i / (double) segs - 0.5;
			double ang = Math.toRadians(-yaw) + t * spread;
			double px = center.x + Math.sin(-ang) * -r;
			double pz = center.z + Math.cos(-ang) * -r;
			Vec3 point = new Vec3(px, center.y + 0.05, pz);
			float[] s = RenderUtil.worldToScreen(point.x, point.y, point.z);
			if (s != null) arcScreen.add(s);
			if (prev != null) {
				float[] a = RenderUtil.worldToScreen(prev.x, prev.y, prev.z);
				if (a != null && s != null) {
					RenderUtil.line(g, a[0], a[1], s[0], s[1], line);
				}
			}
			prev = point;
		}
		float[] c0 = RenderUtil.worldToScreen(center.x, center.y + 0.05, center.z);
		if (c0 != null) {
			for (int i = 0; i + 1 < arcScreen.size(); i++) {
				float[] a = arcScreen.get(i);
				float[] b = arcScreen.get(i + 1);
				RenderUtil.line(g, c0[0], c0[1], a[0], a[1], RenderUtil.withAlpha(color.rgb(), 60));
				if (fill.get()) {
					// лёгкая заливка сектора полосой между точками дуги
					int y0 = (int) Math.min(a[1], b[1]);
					int h = (int) Math.abs(b[1] - a[1]) + 1;
					int x0 = (int) Math.min(a[0], b[0]);
					int w = (int) Math.abs(b[0] - a[0]) + 1;
					RenderUtil.rect(g, x0, y0, Math.max(1, w), Math.max(1, h), argb);
				}
			}
		}
	}
}
