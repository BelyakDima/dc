package dev.darkness.client.ui;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudEditorScreen extends Screen {
	private HudModule dragging;
	private double grabDx, grabDy;

	public HudEditorScreen() {
		super(Component.literal("HUD Editor"));
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		RenderUtil.rect(g, 0, 0, this.width, this.height, 0x6606060C);
		for (HudModule hud : DarknessClient.getModuleManager().getHudModules()) {
			if (hud.isEnabled()) {
				hud.renderEditor(g, this.width, this.height, partialTick, 0xFF7C4DFF);
			}
		}
		String hint = "ЛКМ — перетаскивать · ПКМ по элементу — вкл/выкл · Esc — выход";
		RenderUtil.rect(g, this.width / 2 - this.font.width(hint) / 2 - 6, this.height - 26,
			this.font.width(hint) + 12, 16, 0xE60D0D14);
		RenderUtil.textCentered(g, hint, this.width / 2, this.height - 22, 0xFFB388FF);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x(), my = event.y();
		for (HudModule hud : DarknessClient.getModuleManager().getHudModules()) {
			if (!hud.isEnabled()) continue;
			if (hud.isHovered((int) mx, (int) my, this.width, this.height)) {
				if (event.button() == 1) {
					hud.toggle();
					return true;
				}
				if (event.button() == 0) {
					dragging = hud;
					grabDx = mx - hud.getX(this.width);
					grabDy = my - hud.getY(this.height);
					return true;
				}
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (dragging != null) {
			double nx = (event.x() - grabDx) / this.width;
			double ny = (event.y() - grabDy) / this.height;
			nx = Math.max(0, Math.min(1 - dragging.getWidth() / (double) this.width, nx));
			ny = Math.max(0, Math.min(1 - dragging.getHeight() / (double) this.height, ny));
			dragging.setPosition(nx, ny);
			return true;
		}
		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (dragging != null) {
			DarknessClient.getConfigManager().scheduleSave();
		}
		dragging = null;
		return super.mouseReleased(event);
	}

	@Override
	public void onClose() {
		DarknessClient.getConfigManager().save();
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
