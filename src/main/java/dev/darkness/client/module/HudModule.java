package dev.darkness.client.module;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Base class for draggable HUD elements. Position is stored relative to the
 * screen size (0..1) so elements survive resolution changes.
 */
public abstract class HudModule extends Module {
	private double x = 0.02, y = 0.02; // relative
	private transient double lastW = 60, lastH = 20;
	private final NumberSetting scaleSetting;

	public HudModule(String name, String description) {
		super(name, description, Category.HUD);
		scaleSetting = new NumberSetting("Масштаб", "Размер элемента", 1.0, 0.5, 2.0, 0.05);
		register(scaleSetting);
	}

	public float getHudScale() {
		return scaleSetting.get().floatValue();
	}

	public NumberSetting getScaleSetting() {
		return scaleSetting;
	}

	/** Отрисовка с учётом масштаба (вызывать вместо render). */
	public final void renderScaled(net.minecraft.client.gui.GuiGraphics g, int screenW, int screenH, float partialTick) {
		float s = getHudScale();
		if (Math.abs(s - 1.0f) < 0.01f) {
			render(g, screenW, screenH, partialTick);
			return;
		}
		int x = getX(screenW), y = getY(screenH);
		var pose = g.pose();
		pose.pushMatrix();
		pose.translate((float) x, (float) y);
		pose.scale(s, s);
		pose.translate((float) -x, (float) -y);
		try {
			render(g, screenW, screenH, partialTick);
		} finally {
			pose.popMatrix();
		}
	}

	@Override
	public void toggle() {
		super.toggle();
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
		renderScaled(g, screenW, screenH, partialTick);
		int x = getX(screenW) - 2;
		int y = getY(screenH) - 2;
		int s = Math.round(getHudScale() * 100);
		int w = s != 100 ? (int) Math.ceil(getWidth() * getHudScale()) : getWidth();
		int h = s != 100 ? (int) Math.ceil(getHeight() * getHudScale()) : getHeight();
		g.renderOutline(x, y, w + 4, h + 4, accent);
	}

	public boolean isHovered(int mouseX, int mouseY, int screenW, int screenH) {
		int x = getX(screenW);
		int y = getY(screenH);
		int w = (int) Math.ceil(getWidth() * getHudScale());
		int h = (int) Math.ceil(getHeight() * getHudScale());
		return mouseX >= x - 3 && mouseX <= x + w + 3
			&& mouseY >= y - 3 && mouseY <= y + h + 3;
	}
}
