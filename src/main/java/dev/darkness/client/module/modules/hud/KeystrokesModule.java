package dev.darkness.client.module.modules.hud;

import com.mojang.blaze3d.platform.InputConstants;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.CpManager;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

public class KeystrokesModule extends HudModule {
	private final dev.darkness.client.module.BooleanSetting spaceBar;
	private final dev.darkness.client.module.BooleanSetting showCps;
	private final dev.darkness.client.module.ColorSetting pressedColor;

	public KeystrokesModule() {
		super("Keystrokes", "Кнопки WASD, прыжок, ЛКМ/ПКМ");
		spaceBar = new dev.darkness.client.module.BooleanSetting("Пробел", "Показывать кнопку прыжка", true);
		showCps = new dev.darkness.client.module.BooleanSetting("CPS на кнопках", "Показывать CPS на ЛКМ/ПКМ", true);
		pressedColor = new dev.darkness.client.module.ColorSetting("Цвет нажатия", "Цвет подсветки нажатых клавиш", 0xFF7C4DFF);
		register(spaceBar, showCps, pressedColor);
	}

	private boolean key(int code) {
		return InputConstants.isKeyDown(mc.getWindow(), code);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		int cell = 22, gap = 2;
		int rows = spaceBar.get() ? 4 : 3;
		int w = cell * 3 + gap * 2;
		int h = cell * rows + gap * (rows - 1);
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);

		drawKey(g, "W", x + cell + gap, y, cell, cell, key(GLFW.GLFW_KEY_W));
		drawKey(g, "A", x, y + cell + gap, cell, cell, key(GLFW.GLFW_KEY_A));
		drawKey(g, "S", x + cell + gap, y + cell + gap, cell, cell, key(GLFW.GLFW_KEY_S));
		drawKey(g, "D", x + (cell + gap) * 2, y + cell + gap, cell, cell, key(GLFW.GLFW_KEY_D));

		int mouseRow = y + (cell + gap) * 2;
		String lmb = showCps.get() ? "LMB " + CpManager.leftCps() : "LMB";
		String rmb = showCps.get() ? "RMB " + CpManager.rightCps() : "RMB";
		drawKey(g, lmb, x, mouseRow, cell * 2 - 1 + gap, cell, mc.mouseHandler.isLeftPressed() || CpManager.leftHeld());
		drawKey(g, rmb, x + cell * 2 + gap, mouseRow, cell, cell, mc.mouseHandler.isRightPressed() || CpManager.rightHeld());

		if (spaceBar.get()) {
			int spaceY = y + (cell + gap) * 3;
			boolean jump = key(GLFW.GLFW_KEY_SPACE);
			drawKey(g, "", x, spaceY, w, 10, jump);
			if (jump) {
				RenderUtil.rect(g, x + 4, spaceY + 4, w - 8, 2, 0xFFFFFFFF);
			}
		}
	}

	private void drawKey(GuiGraphics g, String label, int x, int y, int w, int h, boolean pressed) {
		RenderUtil.rect(g, x, y, w, h, pressed ? RenderUtil.withAlpha(pressedColor.rgb(), 0xE0) : 0x900D0D14);
		RenderUtil.outline(g, x, y, w, h, 0x40000000);
		if (!label.isEmpty()) {
			RenderUtil.textCentered(g, label, x + w / 2, y + (h - 8) / 2, pressed ? 0xFFFFFFFF : 0xFFE0E0E0);
		}
	}
}
