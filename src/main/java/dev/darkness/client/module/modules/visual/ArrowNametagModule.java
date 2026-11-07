package dev.darkness.client.module.modules.visual;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;

public class ArrowNametagModule extends Module implements OverlayRenderer {
	public ArrowNametagModule() {
		super("ArrowNametag", "Имя стрелка над стрелами и снарядами", Category.VISUALS);
	}

	@Override
	public void renderOverlay(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.level == null || mc.player == null) return;
		for (Entity e : mc.level.entitiesForRendering()) {
			if (!(e instanceof Projectile proj) || e == mc.player) continue;
			if (e.distanceTo(mc.player) > 48) continue;
			Entity owner = proj.getOwner();
			if (owner == null || owner == mc.player) continue;
			float[] pos = RenderUtil.worldToScreen(e.getX(), e.getY() + e.getBbHeight() + 0.3, e.getZ());
			if (pos == null) continue;
			String text = "→ " + owner.getName().getString();
			int w = mc.font.width(text);
			RenderUtil.rect(g, (int) pos[0] - w / 2 - 2, (int) pos[1] - 1, w + 4, 10, 0x900D0D14);
			RenderUtil.text(g, text, (int) pos[0] - w / 2, (int) pos[1], 0xFFFFD54F);
		}
	}
}
