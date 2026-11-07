package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.ColorSetting;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlockOverlayCustomModule extends Module implements OverlayRenderer {
	private final ColorSetting color;
	private final BooleanSetting fill;
	private final NumberSetting fillAlpha;

	public BlockOverlayCustomModule() {
		super("BlockOverlay", "Своя обводка блока под прицелом (вместо ванильной)", Category.VISUALS);
		color = new ColorSetting("Цвет обводки", "Цвет каркаса", 0xFF7C4DFF);
		fill = new BooleanSetting("Заливка", "Полупрозрачная заливка блока", false);
		fillAlpha = new NumberSetting("Прозрачность заливки", "0-255", 60, 10, 160, 10);
		register(color, fill, fillAlpha);
	}

	@Override
	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null || mc.level == null) return;
		if (!(mc.hitResult instanceof BlockHitResult hit)) return;
		BlockPos pos = hit.getBlockPos();
		if (mc.level.getBlockState(pos).isAir()) return;

		double minX = pos.getX(), minY = pos.getY(), minZ = pos.getZ();
		double maxX = minX + 1, maxY = minY + 1, maxZ = minZ + 1;
		float[][] c = new float[8][];
		int i = 0;
		for (double yy : new double[]{minY, maxY}) {
			for (double xx : new double[]{minX, maxX}) {
				for (double zz : new double[]{minZ, maxZ}) {
					float[] s = RenderUtil.worldToScreen(xx, yy, zz);
					if (s == null) return;
					c[i++] = s;
				}
			}
		}
		int line = RenderUtil.withAlpha(color.rgb(), 230);
		int[][] edges = {{0,1},{1,3},{3,2},{2,0},{4,5},{5,7},{7,6},{6,4},{0,4},{1,5},{3,7},{2,6}};
		for (int[] e : edges) {
			RenderUtil.line(g, c[e[0]][0], c[e[0]][1], c[e[1]][0], c[e[1]][1], line);
		}
		if (fill.get()) {
			float left = Float.MAX_VALUE, right = -Float.MAX_VALUE, top = Float.MAX_VALUE, bottom = -Float.MAX_VALUE;
			for (float[] p : c) {
				left = Math.min(left, p[0]);
				right = Math.max(right, p[0]);
				top = Math.min(top, p[1]);
				bottom = Math.max(bottom, p[1]);
			}
			RenderUtil.rect(g, (int) left, (int) top, (int) (right - left), (int) (bottom - top),
				RenderUtil.withAlpha(color.rgb(), (int) Math.round(fillAlpha.get())));
		}
	}
}
