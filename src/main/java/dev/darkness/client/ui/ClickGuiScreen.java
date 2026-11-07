package dev.darkness.client.ui;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.util.KeyManager;
import dev.darkness.client.util.RenderUtil;
import dev.darkness.client.util.RoundedUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * Меню в стиле PulseVisuals: тёмная панель по центру, вкладки Visuals/HUD/Utilities,
 * двухколоночная сетка модулей с пилюлями, поиск справа, док иконок внизу.
 */
public class ClickGuiScreen extends Screen {
	private int currentTab = 0; // 0 Visuals, 1 HUD, 2 Utilities
	private Module expanded;
	private Module listeningBind;
	private double scroll;
	private EditBox searchBox;

	public boolean isListeningBind() {
		return listeningBind != null;
	}

	public boolean isSearchFocused() {
		return searchBox != null && searchBox.isFocused();
	}

	private record Area(int x, int y, int w, int h, Runnable click, BiConsumer<Double, Double> drag) {
	}

	private final List<Area> areas = new ArrayList<>();
	private Area dragArea = null;

	private static final int ROW_H = 30;
	private static final int COL_GAP = 10;
	private static final int ACCENT = 0xFF8C6BFF;
	private static final int BG = 0xF0101016;
	private static final int ROW_BG = 0xF0181820;
	private static final int ROW_BG_DIM = 0xC0121218;
	private static final int BORDER = 0x402A2A3A;

	private record SettingRow(int x, int y, int w, int h, Runnable click, BiConsumer<Double, Double> drag,
							  RowRenderer renderer) {
	}

	private interface RowRenderer {
		void render(GuiGraphics g, int mx);
	}

	public ClickGuiScreen() {
		super(Component.literal("Darkness Client"));
	}

	@Override
	protected void init() {
		super.init();
		searchBox = new EditBox(this.font, 0, 0, 110, 13, Component.literal("Поиск"));
		searchBox.setMaxLength(24);
		searchBox.setHint(Component.literal("Поиск"));
		searchBox.setCanLoseFocus(true);
	}

	private List<Module> modulesForTab(int tab) {
		if (tab == 1) {
			return DarknessClient.getModuleManager().byCategory(Category.HUD);
		}
		if (tab == 2) {
			// Utilities включает оптимизацию
			List<Module> out = new ArrayList<>(DarknessClient.getModuleManager().byCategory(Category.UTILITY));
			out.addAll(DarknessClient.getModuleManager().byCategory(Category.OPTIMIZATION));
			out.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
			return out;
		}
		return DarknessClient.getModuleManager().byCategory(Category.VISUALS);
	}

	private List<Module> visibleModules() {
		String q = searchBox.getValue().trim().toLowerCase(Locale.ROOT);
		List<Module> base = modulesForTab(currentTab);
		if (q.isBlank()) return base;
		List<Module> out = new ArrayList<>();
		for (Module m : DarknessClient.getModuleManager().getModules()) {
			if (m.getName().toLowerCase(Locale.ROOT).contains(q)) out.add(m);
		}
		out.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		return out;
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		areas.clear();
		int w = this.width, h = this.height;
		RenderUtil.rect(g, 0, 0, w, h, 0x8806060C);

		int pw = Math.min(460, w - 24);
		int px = (w - pw) / 2;
		int py = Math.max(20, h / 2 - 220);
		int ph = Math.min(h - py - 20, 470);
		if (ph < 200) ph = Math.min(h - 40, 300);
		// панель
		RenderUtil.rect(g, px, py, pw, ph, BG);
		RenderUtil.rect(g, px + 2, py - 2, pw - 4, 2, BG);
		RenderUtil.rect(g, px + 2, py + ph, pw - 4, 2, BG);
		RenderUtil.outline(g, px, py, pw, ph, BORDER);

		// --- верх: вкладки слева, поиск справа ---
		int tabsY = py + 8;
		String[] tabs = {"Visuals", "HUD", "Utilities"};
		int tx = px + 14;
		for (int i = 0; i < tabs.length; i++) {
			boolean active = currentTab == i && searchBox.getValue().isBlank();
			int color = active ? 0xFFFFFFFF : 0xFF6E6E7A;
			RenderUtil.text(g, tabs[i], tx, tabsY, color);
			int tw = this.font.width(tabs[i]);
			if (active) {
				RenderUtil.rect(g, tx, tabsY + 10, tw, 1, 0xFFFFFFFF);
			}
			int fx = tx;
			int fi = i;
			registerArea(fx, tabsY - 2, tw + 6, 16, () -> {
				currentTab = fi;
				expanded = null;
				scroll = 0;
				if (!searchBox.getValue().isEmpty()) searchBox.setValue("");
			}, null);
			tx += tw + 18;
		}

		int sbx = px + pw - 150, sby = py + 5;
		searchBox.setX(sbx + 14);
		searchBox.setY(sby + 1);
		// иконка лупы
		RenderUtil.rect(g, sbx + 4, sby + 4, 5, 5, 0xFF8A8A96);
		RenderUtil.rect(g, sbx + 8, sby + 8, 3, 1, 0xFF8A8A96);
		RenderUtil.rect(g, sbx + 10, sby + 9, 1, 1, 0xFF8A8A96);
		// иконка карандаша
		RenderUtil.rect(g, sbx + 134, sby + 3, 1, 8, 0xFF55556A);
		RenderUtil.rect(g, sbx + 132, sby + 11, 4, 2, 0xFF55556A);
		searchBox.render(g, mouseX, mouseY, partialTick);

		registerArea(searchBox.getX(), searchBox.getY(), searchBox.getWidth(), searchBox.getHeight(),
			() -> setFocused(searchBox), null);

		// --- сетка модулей ---
		List<Module> modules = visibleModules();
		int gridX = px + 14;
		int gridW = pw - 28;
		int colW = (gridW - COL_GAP) / 2;
		int gridTop = tabsY + 20;
		int gridBottom = py + ph - 44;
		int viewport = Math.max(60, gridBottom - gridTop);

		int[] heights = new int[modules.size()];
		int totalH = 0;
		for (int i = 0; i < modules.size(); i++) {
			heights[i] = rowHeight(modules.get(i));
			totalH += heights[i];
		}
		boolean scrollable = totalH > viewport;
		if (scrollable) {
			scroll = Math.max(0, Math.min(scroll, totalH - viewport));
		} else {
			scroll = 0;
		}

		// заполнение: две колонки сбалансированно по высоте
		List<Module> col1 = new ArrayList<>();
		List<Module> col2 = new ArrayList<>();
		int[] heights1 = new int[modules.size()];
		int[] heights2 = new int[modules.size()];
		int h1 = 0, h2 = 0;
		boolean left = true;
		for (int i = 0; i < modules.size(); i++) {
			int rh = heights[i];
			if (left) {
				col1.add(modules.get(i));
				heights1[col1.size() - 1] = rh;
				h1 += rh;
			} else {
				col2.add(modules.get(i));
				heights2[col2.size() - 1] = rh;
				h2 += rh;
			}
			left = h1 <= h2;
		}

		int[] scrollAdj = {0, 0};
		scrollAdj[0] = (int) scroll;
		scrollAdj[1] = (int) Math.max(0, Math.min(scroll, Math.max(0, h2 - viewport)));

		for (int col = 0; col < 2; col++) {
			List<Module> list = col == 0 ? col1 : col2;
			int[] hts = col == 0 ? heights1 : heights2;
			int cx = gridX + col * (colW + COL_GAP);
			int cy = gridTop - scrollAdj[col];
			for (int i = 0; i < list.size(); i++) {
				Module m = list.get(i);
				int rh = hts[i];
				if (cy + rh >= gridTop - 2 && cy <= gridBottom) {
					drawModuleRow(g, m, cx, cy, colW, rh, mouseX);
				}
				cy += rh;
			}
		}

		// --- нижний док ---
		int dockY = py + ph - 38;
		int dockW = 5 * 34 + 8;
		int dockX = w / 2 - dockW / 2;
		RenderUtil.rect(g, dockX - 4, dockY - 4, dockW + 8, 38, BG);
		String[] icons = {"◆", "⬡", "◈", "▤", "✦"};
		for (int i = 0; i < 5; i++) {
			int ix = dockX + i * 34 + 4;
			boolean activeDock = (i >= 1 && i <= 3 && currentTab == i - 1);
			int bgc = activeDock ? ACCENT : 0xFF1C1C26;
			RenderUtil.rect(g, ix, dockY, 30, 30, bgc);
			RenderUtil.rect(g, ix + 1, dockY + 1, 28, 28, bgc);
			RenderUtil.textCentered(g, icons[i], ix + 15, dockY + 11, activeDock ? 0xFFFFFFFF : 0xFF9E9E9E);
			int ii = i;
			registerArea(ix, dockY, 30, 30, () -> {
				switch (ii) {
					case 1 -> {
						currentTab = 0;
						searchBox.setValue("");
					}
					case 2 -> {
						currentTab = 1;
						searchBox.setValue("");
					}
					case 3 -> {
						currentTab = 2;
						searchBox.setValue("");
					}
					case 4 -> minecraft.setScreen(new AccountsScreen());
				}
			}, null);
		}

		if (listeningBind != null) {
			String hint = "Нажмите клавишу для " + listeningBind.getName() + " (Esc — убрать)";
			int hw = this.font.width(hint) + 12;
			RenderUtil.rect(g, w / 2 - hw / 2, h - 44, hw, 14, 0xE60D0D14);
			RenderUtil.textCentered(g, hint, w / 2, h - 40, 0xFFB388FF);
		}
	}

	private void drawModuleRow(GuiGraphics g, Module m, int x, int y, int w, int fullH, int mouseX) {
		boolean on = m.isEnabled();
		int bg = on ? ROW_BG : ROW_BG_DIM;
		RenderUtil.rect(g, x, y, w, ROW_H, bg);
		RenderUtil.rect(g, x + 1, y + 1, w - 2, ROW_H - 2, bg);
		RenderUtil.outline(g, x, y, w, ROW_H, on ? RenderUtil.withAlpha(ACCENT, 90) : BORDER);
		int textColor = on ? 0xFFF0F0F0 : 0xFF6E6E7A;
		RenderUtil.text(g, m.getName(), x + 6, y + (ROW_H - 8) / 2, textColor);
		int pillX = x + w - 30, pillY = y + (ROW_H - 10) / 2;
		RoundedUI.togglePill(g, pillX, pillY, on);
		registerArea(pillX - 2, pillY - 3, 26, 16, m::toggle, null);
		Module mm = m;
		registerArea(x, y, w - 34, ROW_H, () -> {
			expanded = expanded == mm ? null : mm;
			listeningBind = null;
		}, null);

		if (m == expanded && fullH > ROW_H) {
			int sy = y + ROW_H;
			int sh = fullH - ROW_H;
			RenderUtil.rect(g, x, sy, w, sh, 0xF00D0D12);
			RenderUtil.rect(g, x, sy, 1, sh, RenderUtil.withAlpha(ACCENT, 120));
			for (SettingRow row : settingRows(m, x + 4, sy + 2, w - 8)) {
				if (row.y() > y + fullH) break;
				row.renderer().render(g, mouseX);
				registerArea(row.x(), row.y(), row.w(), row.h(), row.click(), row.drag());
			}
		}
	}

	private int rowHeight(Module m) {
		if (m != expanded) return ROW_H;
		int rows = m.getSettings().size() + 1;
		return ROW_H + rows * 15 + 4;
	}

	private List<SettingRow> settingRows(Module m, int x, int startY, int w) {
		List<SettingRow> rows = new ArrayList<>();
		int y = startY;
		for (var s : m.getSettings()) {
			int ry = y;
			int rw = w;
			if (s instanceof dev.darkness.client.module.BooleanSetting bs) {
				rows.add(new SettingRow(x, ry, rw, 13,
					() -> bs.set(!bs.get()), null,
					(g, mm) -> {
						RenderUtil.text(g, s.getName(), x + 4, ry + 2, 0xFFC8C8C8);
						RenderUtil.textRight(g, bs.get() ? "ВКЛ" : "ВЫКЛ", x + rw - 4, ry + 2,
							bs.get() ? 0xFF66BB6A : 0xFF757575);
					}));
			} else if (s instanceof NumberSetting ns) {
				final int fx = x + 4;
				final int fw = rw - 8;
				final int fy = ry;
				BiConsumer<Double, Double> apply = (dmx, dmy) -> {
					double t = Math.max(0, Math.min(1, (dmx - fx) / fw));
					ns.set(ns.getMin() + t * (ns.getMax() - ns.getMin()));
				};
				rows.add(new SettingRow(x, ry, rw, 15, null, apply, (g, mm) -> {
					RenderUtil.text(g, s.getName(), x + 4, fy + 2, 0xFFC8C8C8);
					RenderUtil.textRight(g, ns.getDisplayValue(), x + rw - 4, fy + 2, 0xFFB388FF);
					int barY = fy + 12;
					double t = (ns.get() - ns.getMin()) / (ns.getMax() - ns.getMin());
					RenderUtil.rect(g, fx, barY, fw, 2, 0x4DFFFFFF);
					RenderUtil.rect(g, fx, barY, (int) (fw * Math.max(0, Math.min(1, t))), 2, ACCENT);
				}));
			} else if (s instanceof dev.darkness.client.module.ModeSetting ms) {
				rows.add(new SettingRow(x, ry, rw, 13, ms::cycle, null, (g, mm) -> {
					RenderUtil.text(g, s.getName(), x + 4, ry + 2, 0xFFC8C8C8);
					RenderUtil.textRight(g, ms.get(), x + rw - 4, ry + 2, 0xFFB388FF);
				}));
			} else if (s instanceof dev.darkness.client.module.ColorSetting cs) {
				rows.add(new SettingRow(x, ry, rw, 13, () -> {
					cs.shiftHue(45);
				}, null, (g, mm) -> {
					RenderUtil.text(g, s.getName(), x + 4, ry + 2, 0xFFC8C8C8);
					RenderUtil.rect(g, x + rw - 14, ry + 3, 10, 7, cs.rgb());
				}));
			} else if (s instanceof dev.darkness.client.module.StringSetting ss) {
				rows.add(new SettingRow(x, ry, rw, 13, null, null, (g, mm) -> {
					RenderUtil.text(g, s.getName(), x + 4, ry + 2, 0xFFC8C8C8);
					RenderUtil.textRight(g, truncate(ss.get(), 18), x + rw - 4, ry + 2, 0xFFB388FF);
				}));
			}
			y += 15;
		}
		int by = y;
		String keyLabel = m.getKeybind() > 0 ? KeyManager.keyName(m.getKeybind()) : "—";
		rows.add(new SettingRow(x, by, w, 13, () -> listeningBind = m, null, (g, mm) -> {
			RenderUtil.text(g, "Клавиша", x + 4, by + 2, 0xFFC8C8C8);
			RenderUtil.textRight(g, listeningBind == m ? "..." : keyLabel, x + w - 4, by + 2, 0xFFB388FF);
		}));
		return rows;
	}

	private static String truncate(String s, int max) {
		return s.length() > max ? s.substring(0, max - 1) + "…" : s;
	}

	private void registerArea(int x, int y, int w, int h, Runnable click, BiConsumer<Double, Double> drag) {
		if (click == null && drag == null) return;
		areas.add(new Area(x, y, w, h, click, drag));
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x(), my = event.y();
		if (mx >= searchBox.getX() && mx <= searchBox.getX() + searchBox.getWidth()
			&& my >= searchBox.getY() && my <= searchBox.getY() + searchBox.getHeight()) {
			setFocused(searchBox);
			searchBox.setFocused(true);
			return searchBox.mouseClicked(event, doubleClick);
		}
		if (getFocused() == searchBox) {
			setFocused(null);
			searchBox.setFocused(false);
		}
		if (event.button() == 0) {
			for (int i = areas.size() - 1; i >= 0; i--) {
				Area a = areas.get(i);
				if (mx >= a.x() && mx <= a.x() + a.w() && my >= a.y() && my <= a.y() + a.h()) {
					if (a.click() != null) a.click().run();
					if (a.drag() != null) {
						a.drag().accept(mx, my);
						dragArea = a;
					}
					return true;
				}
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (dragArea != null) {
			dragArea.drag().accept(event.x(), event.y());
			return true;
		}
		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		dragArea = null;
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		scroll -= scrollY * 24;
		return super.mouseScrolled(x, y, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (listeningBind != null) {
			if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
				listeningBind.setKeybind(-1);
			} else {
				listeningBind.setKeybind(event.key());
			}
			listeningBind = null;
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
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
