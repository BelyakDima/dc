package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TrajectoriesModule extends Module {
	private final ColorSetting lineColor;
	private final ColorSetting hitColor;
	private final BooleanSetting showHitBox;

	public TrajectoriesModule() {
		super("Trajectories", "Предсказание траектории снаряда", Category.VISUALS);
		lineColor = new ColorSetting("Цвет линии", "Цвет траектории", 0xFF7C4DFF);
		hitColor = new ColorSetting("Цвет попадания", "Цвет точки приземления", 0xFFFF5252);
		showHitBox = new BooleanSetting("Точка попадания", "Показывать место падения", true);
		register(lineColor, hitColor, showHitBox);
	}

	private enum ProjType {
		NONE, ARROW, THROWABLE
	}

	private ProjType currentType() {
		if (mc.player == null) return ProjType.NONE;
		Item item = mc.player.getMainHandItem().getItem();
		if (item instanceof BowItem) {
			float draw = mc.player.getUseItem() == mc.player.getMainHandItem()
				? BowItem.getPowerForTime(mc.player.getUseItemRemainingTicks()) : 0f;
			if (draw > 0.1f) return ProjType.ARROW;
			return ProjType.NONE;
		}
		if (item instanceof CrossbowItem || item instanceof TridentItem) return ProjType.ARROW;
		if (item instanceof SnowballItem || item instanceof EnderpearlItem || item instanceof EggItem || item instanceof ThrowablePotionItem) {
			return ProjType.THROWABLE;
		}
		return ProjType.NONE;
	}

	public void renderOverlay(GuiGraphics g) {
		ProjType type = currentType();
		if (type == ProjType.NONE || mc.player == null) return;

		float power = 1.5f;
		if (type == ProjType.THROWABLE) power = 1.5f;
		if (mc.player.getUseItem().getItem() instanceof BowItem) {
			float draw = BowItem.getPowerForTime(mc.player.getUseItemRemainingTicks());
			power = Math.max(0.1f, draw) * 3.0f;
		}

		double gravity = type == ProjType.ARROW ? 0.05 : 0.03;
		double drag = type == ProjType.ARROW ? 0.99 : 0.99;

		Vec3 eye = mc.player.getEyePosition(1.0f);
		Vec3 look = mc.player.getViewVector(1.0f);
		double vx = look.x * power, vy = look.y * power, vz = look.z * power;

		List<float[]> points = new ArrayList<>();
		float[] hitPoint = null;
		double x = eye.x, y = eye.y, z = eye.z;

		int steps = 120;
		for (int i = 0; i < steps; i++) {
			double px = x, py = y, pz = z;
			// подшаги интеграции для точности
			for (int sub = 0; sub < 3; sub++) {
				vy -= gravity;
				x += vx;
				y += vy;
				z += vz;
				vx *= drag;
				vy *= drag;
				vz *= drag;
			}
			float[] a = RenderUtil.worldToScreen(px, py, pz);
			float[] b = RenderUtil.worldToScreen(x, y, z);
			if (a != null && b != null) {
				points.add(a);
				points.add(b);
			}

			if (mc.level != null && !mc.level.noCollision(mc.player,
				new net.minecraft.world.phys.AABB(Math.min(px, x), Math.min(py, y), Math.min(pz, z),
					Math.max(px, x), Math.max(py, y), Math.max(pz, z)))) {
				hitPoint = b;
				break;
			}
		}

		int c = lineColor.rgb();
		for (int i = 0; i + 1 < points.size(); i += 2) {
			float alpha = 0.35f + 0.65f * (i / (float) points.size());
			RenderUtil.line(g, points.get(i)[0], points.get(i)[1], points.get(i + 1)[0], points.get(i + 1)[1],
				RenderUtil.withAlpha(c, (int) (255 * alpha)));
		}
		if (showHitBox.get() && hitPoint != null) {
			int hc = hitColor.rgb();
			for (int dx = -3; dx <= 3; dx++) {
				for (int dy = -3; dy <= 3; dy++) {
					if (dx * dx + dy * dy <= 9) {
						RenderUtil.rect(g, (int) hitPoint[0] + dx, (int) hitPoint[1] + dy, 1, 1, hc);
					}
				}
			}
		}
	}
}
