package dev.darkness.client.module.modules.visual;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HitParticlesModule extends Module implements OverlayRenderer {
	public static class Spark {
		public double x, y, z;
		public double vx, vy, vz;
		public long created = System.currentTimeMillis();
		public final boolean crit;

		Spark(double x, double y, double z, boolean crit, Random rnd) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.crit = crit;
			this.vx = (rnd.nextDouble() - 0.5) * 0.35;
			this.vy = rnd.nextDouble() * 0.4 + 0.05;
			this.vz = (rnd.nextDouble() - 0.5) * 0.35;
		}
	}

	private final ColorSetting hitColor = new ColorSetting("Цвет удара", "Цвет обычных искр", 0xFFFFD54F);
	private final ColorSetting critColor = new ColorSetting("Цвет крита", "Цвет критических искр", 0xFFFF5252);
	private final NumberSetting count = new NumberSetting("Количество", "Искр за попадание", 8, 2, 20, 1);
	private final NumberSetting life = new NumberSetting("Время жизни", "Мс до исчезновения", 500, 200, 1200, 100);
	private final List<Spark> sparks = new ArrayList<>();
	private final Random random = new Random();
	private final dev.darkness.client.util.TargetManager.DamageListener listener;

	public HitParticlesModule() {
		super("HitParticles", "Искры при попадании по цели", Category.VISUALS);
		register(hitColor, critColor, count, life);
		listener = (amount, x, y, z, crit) -> {
			if (!isEnabled()) return;
			int n = (int) Math.round(count.get());
			synchronized (sparks) {
				for (int i = 0; i < n; i++) {
					sparks.add(new Spark(x + (random.nextDouble() - 0.5) * 0.6,
						y + random.nextDouble() * 0.4,
						z + (random.nextDouble() - 0.5) * 0.6, crit, random));
				}
				while (sparks.size() > 200) sparks.remove(0);
			}
		};
	}

	@Override
	protected void onEnable() {
		DarknessClient.getTargetManager().addListener(listener);
	}

	@Override
	protected void onDisable() {
		DarknessClient.getTargetManager().removeListener(listener);
	}

	public void tick() {
		synchronized (sparks) {
			sparks.removeIf(s -> System.currentTimeMillis() - s.created > life.get());
			for (Spark s : sparks) {
				s.vy -= 0.02;
				s.x += s.vx;
				s.y += s.vy;
				s.z += s.vz;
			}
		}
	}

	@Override
	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		synchronized (sparks) {
			for (Spark s : sparks) {
				float[] pos = RenderUtil.worldToScreen(s.x, s.y, s.z);
				if (pos == null) continue;
				long age = System.currentTimeMillis() - s.created;
				float t = age / life.get().floatValue();
				int alpha = (int) (255 * (1 - t));
				int color = RenderUtil.withAlpha(s.crit ? critColor.rgb() : hitColor.rgb(), Math.max(0, alpha));
				int size = Math.max(1, Math.round(2 * (1 - t * 0.5f)));
				RenderUtil.rect(g, (int) pos[0], (int) pos[1], size, size, color);
			}
		}
	}
}
