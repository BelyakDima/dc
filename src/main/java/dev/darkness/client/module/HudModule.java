package dev.darkness.client.module;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Base class for draggable HUD elements. Position is stored relative to the
 * screen size (0..1) so elements survive resolution changes.
 */
public abstract class HudModule extends Module {
	private double x = 0.02, y = 0.02; // relative
	private transient double lastW = 60, lastH = 20;

	public HudModule(String name, String description) {
		super(name, description, Category.HUD);
	}

	public void setPosition(double relX, double relY) {
		this.x = Math.max(0, Math.min(1, relX));
		this.y = Math.max(0, Math.min(1, relY));
	}

	public double getRelX() {
		return x;
	}

	public double getRelY() {
		return y;
	}

	public int getX(int screenW) {
		return (int) (x * screenW);
	}

	public int getY(int screenH) {
		return (int) (y * screenH);
	}

	public void setSize(int w, int h) {
		this.lastW = w;
		this.lastH = h;
	}

	public int getWidth() {
		return (int) lastW;
	}

	public int getHeight() {
		return (int) lastH;
	}

	/** Draw the element. Implementations must call setSize(w, h). */
	public abstract void render(GuiGraphics g, int screenW, int screenH, float partialTick);

	/** Render inside HUD editor (with border). */
	public final void renderEditor(GuiGraphics g, int screenW, int screenH, float partialTick, int accent) {
		render(g, screenW, screenH, partialTick);
		int x = getX(screenW) - 2;
		int y = getY(screenH) - 2;
		g.renderOutline(x, y, getWidth() + 4, getHeight() + 4, accent);
	}

	public boolean isHovered(int mouseX, int mouseY, int screenW, int screenH) {
		int x = getX(screenW);
		int y = getY(screenH);
		return mouseX >= x - 3 && mouseX <= x + getWidth() + 3
			&& mouseY >= y - 3 && mouseY <= y + getHeight() + 3;
	}
}
