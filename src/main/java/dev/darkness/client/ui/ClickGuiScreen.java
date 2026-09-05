package dev.darkness.client.ui;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.NumberSetting;
import dev.darkness.client.util.KeyManager;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ClickGuiScreen extends Screen {
	private static final int[] PALETTE = {
		0xFF7C4DFF, 0xFF3D5AFE, 0xFF00B0FF, 0xFF00E676, 0xFFFFEB3B,
		0xFFFF9100, 0xFFFF5252, 0xFFFF4081, 0xFFE040FB, 0xFFB388FF,
		0xFFFFFFFF, 0xFF212121
	};

	private Category current = Category.COMBAT;
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

	private static final int PANEL_X = 12, PANEL_Y = 26, PANEL_W = 200;
	private static final int ROW = 16;          // высота строки модуля
	private static final int SET_ROW = 14;      // высота строки настройки
	private static final int TAB_W = 92, TAB_H = 15;

	public ClickGuiScreen() {
		super(Component.literal("Darkness Client"));
	}

	@Override
	protected void init() {
		super.init();
		searchBox = new EditBox(this.font, PANEL_X, 6, 104, 12, Component.literal("Поиск"));
		searchBox.setMaxLength(24);
		searchBox.setHint(Component.literal("Поиск..."));
		searchBox.setCanLoseFocus(true);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		areas.clear();
		int w = this.width, h = this.height;
		RenderUtil.rect(g, 0, 0, w, h, 0x8806060C);

		// --- верхняя панель: заголовок, поиск, кнопки ---
		RenderUtil.text(g, DarknessClient.MOD_NAME, 12, 8, 0xFFB388FF);
		int tx = 12 + this.font.width(DarknessClient.MOD_NAME) + 8;
		searchBox.render(g, mouseX, mouseY, partialTick);

		int barY = 6, barH = 13;
		int bx = w - 4;
		bx -= 84; actionButton(g, bx, barY, 84, barH, "Customize", () -> {
			dev.darkness.client.util.CustomizeManager.openFolder();
			DarknessClient.getNotifications().show("Положите title.png в папку customize");
		});
		bx -= 84; actionButton(g, bx, barY, 84, barH, "Сохр. конфиг", () -> {
			DarknessClient.getConfigManager().save();
			DarknessClient.getNotifications().show("§aКонфиг сохранён");
		});
		bx -= 84; actionButton(g, bx, barY, 84, barH, "Аккаунты", () -> minecraft.setScreen(new AccountsScreen()));
		bx -= 84; actionButton(g, bx, barY, 84, barH, "HUD-редактор", () -> minecraft.setScreen(new HudEditorScreen()));
		String ver = "v" + DarknessClient.VERSION;
		RenderUtil.textRight(g, ver, bx - 6, barY + 3, 0xFF777777);

		// --- категории ---
		int catY = PANEL_Y;
		for (Category c : Category.values()) {
			boolean active = c == current && searchBox.getValue().isBlank();
			int bg = active ? RenderUtil.withAlpha(c.getColor(), 230) : 0xD90D0D14;
			RenderUtil.rect(g, PANEL_X, catY, TAB_W, TAB_H, bg);
			RenderUtil.outline(g, PANEL_X, catY, TAB_W, TAB_H, active ? 0xFFFFFFFF : 0x30FFFFFF);
			RenderUtil.textCentered(g, c.getDisplayName(), PANEL_X + TAB_W / 2, catY + 4,
				active ? 0xFFFFFFFF : 0xFFD0D0D0);
			int cy = catY;
			registerArea(PANEL_X, cy, TAB_W, TAB_H, () -> {
				current = c;
				expanded = null;
				scroll = 0;
				searchBox.setValue("");
			}, null);
			catY += TAB_H + 2;
		}

		// --- список модулей (листание только когда контент не влезает) ---
		List<Module> modules = visibleModules();
		int px = PANEL_X + TAB_W + 6, py = PANEL_Y;
		int totalH = 0;
		for (Module m : modules) totalH += rowHeight(m);
		int viewport = this.height - py - 6;
		boolean scrollable = totalH > viewport;
		if (scrollable) {
			scroll = Math.max(0, Math.min(scroll, totalH - viewport));
		} else {
			scroll = 0;
		}
		int clipBottom = this.height - (scrollable ? 6 : 2);
		int skipped = 0;
		int rowIdx = 0;
		for (Module m : modules) {
			int rh = rowHeight(m);
			if (scrollable && skipped + rh <= scroll) {
				skipped += rh;
				continue;
			}
			int offset = skipped - (int) scroll;
			skipped += rh;
			int yy = py + offset;
			if (yy > clipBottom) break;

			boolean on = m.isEnabled();
			RenderUtil.rect(g, px, yy, PANEL_W, ROW - 2, on ? RenderUtil.withAlpha(current.getColor(), 60) : 0xB30D0D14);
			RenderUtil.outline(g, px, yy, PANEL_W, ROW - 2, on ? current.getColor() : 0x30FFFFFF);
			String label = m.getName();
			RenderUtil.text(g, label, px + 5, yy + 4, on ? 0xFFFFFFFF : 0xFFC8C8C8);
			String info = m.getDisplayInfo();
			if (info != null) {
				RenderUtil.textRight(g, info, px + PANEL_W - (m == expanded ? 46 : 8), yy + 4, 0xFF9E9E9E);
			}
			if (m == expanded) {
				RenderUtil.textRight(g, "▾", px + PANEL_W - 6, yy + 4, 0xFF9E9E9E);
			}
			Module mm = m;
			registerArea(px, yy, PANEL_W, ROW - 2, () -> {
				expanded = expanded == mm ? null : mm;
				listeningBind = null;
			}, null);

			// переключатель-пилюля
			int pillX = px + PANEL_W - 22, pillY = yy + 4;
			RenderUtil.rect(g, pillX, pillY, 16, 5, on ? current.getColor() : 0xFF404040);
			RenderUtil.rect(g, on ? pillX + 11 : pillX, pillY - 1, 5, 7, 0xFFE0E0E0);
			registerArea(pillX, pillY - 2, 16, 9, mm::toggle, null);

			if (m == expanded) {
				int sy = yy + ROW - 1;
				for (SettingRow row : settingRows(m, px + 4, sy)) {
					if (row.y() > clipBottom) break;
					row.renderer().render(g, mouseX);
					registerArea(row.x(), row.y(), row.w(), row.h(), row.click(), row.drag());
				}
			}
			rowIdx++;
		}

		if (listeningBind != null) {
			String hint = "Нажмите клавишу для " + listeningBind.getName() + " (Esc — убрать)";
			int hw = this.font.width(hint) + 12;
			RenderUtil.rect(g, w / 2 - hw / 2, h - 44, hw, 14, 0xE60D0D14);
			RenderUtil.textCentered(g, hint, w / 2, h - 40, 0xFFB388FF);
		}
	}

	private void actionButton(GuiGraphics g, int x, int y, int w, int h, String label, Runnable action) {
		RenderUtil.rect(g, x, y, w, h, 0xB3161622);
		RenderUtil.outline(g, x, y, w, h, 0x40FFFFFF);
		RenderUtil.textCentered(g, label, x + w / 2, y + (h - 8) / 2, 0xFFE0E0E0);
		registerArea(x, y, w, h, action, null);
	}

	private List<Module> visibleModules() {
		String q = searchBox.getValue().trim().toLowerCase();
		if (!q.isBlank()) {
			List<Module> out = new ArrayList<>();
			for (Module m : DarknessClient.getModuleManager().getModules()) {
				if (m.getName().toLowerCase().contains(q)) out.add(m);
			}
			return out;
		}
		return DarknessClient.getModuleManager().byCategory(current);
	}

	private int rowHeight(Module m) {
		if (m != expanded) return ROW;
		return ROW - 1 + m.getSettings().size() * (SET_ROW + 1) + SET_ROW + 2;
	}

	private record SettingRow(int x, int y, int w, int h, Runnable click, BiConsumer<Double, Double> drag,
							  RowRenderer renderer) {
	}

	private interface RowRenderer {
		void render(GuiGraphics g, int mouseX);
	}

	private List<SettingRow> settingRows(Module m, int x, int startY) {
		List<SettingRow> rows = new ArrayList<>();
		int y = startY;
		int w = PANEL_W - 8;
		for (var s : m.getSettings()) {
			int ry = y;
			if (s instanceof dev.darkness.client.module.BooleanSetting bs) {
				rows.add(new SettingRow(x, ry, w, SET_ROW,
					() -> bs.set(!bs.get()), null,
					(g, mm) -> {
						RenderUtil.rect(g, x, ry, w, SET_ROW, 0x880D0D14);
						RenderUtil.text(g, s.getName(), x + 4, ry + 3, 0xFFC8C8C8);
						RenderUtil.textRight(g, bs.get() ? "ВКЛ" : "ВЫКЛ", x + w - 4, ry + 3,
							bs.get() ? 0xFF66BB6A : 0xFF757575);
					}));
			} else if (s instanceof NumberSetting ns) {
				final int fx = x + 4;
				final int fw = w - 8;
				final int fy = ry;
				BiConsumer<Double, Double> apply = (dmx, dmy) -> {
					double t = Math.max(0, Math.min(1, (dmx - fx) / fw));
					ns.set(ns.getMin() + t * (ns.getMax() - ns.getMin()));
				};
				rows.add(new SettingRow(x, ry, w, SET_ROW, null, apply, (g, mm) -> {
					RenderUtil.rect(g, x, fy, w, SET_ROW, 0x880D0D14);
					RenderUtil.text(g, s.getName(), x + 4, fy + 3, 0xFFC8C8C8);
					RenderUtil.textRight(g, ns.getDisplayValue(), x + w - 4, fy + 3, 0xFFB388FF);
					int barY = fy + SET_ROW - 2;
					double t = (ns.get() - ns.getMin()) / (ns.getMax() - ns.getMin());
					RenderUtil.rect(g, fx, barY, fw, 2, 0x4DFFFFFF);
					RenderUtil.rect(g, fx, barY, (int) (fw * Math.max(0, Math.min(1, t))), 2, current.getColor());
				}));
			} else if (s instanceof dev.darkness.client.module.ModeSetting ms) {
				rows.add(new SettingRow(x, ry, w, SET_ROW, ms::cycle, null, (g, mm) -> {
					RenderUtil.rect(g, x, ry, w, SET_ROW, 0x880D0D14);
					RenderUtil.text(g, s.getName(), x + 4, ry + 3, 0xFFC8C8C8);
					RenderUtil.textRight(g, ms.get(), x + w - 4, ry + 3, 0xFFB388FF);
				}));
			} else if (s instanceof dev.darkness.client.module.ColorSetting cs) {
				rows.add(new SettingRow(x, ry, w, SET_ROW, () -> {
					int idx = 0;
					for (int i = 0; i < PALETTE.length; i++) {
						if ((PALETTE[i] & 0xFFFFFF) == (cs.get() & 0xFFFFFF)) {
							idx = i;
							break;
						}
					}
					cs.set(PALETTE[(idx + 1) % PALETTE.length]);
				}, null, (g, mm) -> {
					RenderUtil.rect(g, x, ry, w, SET_ROW, 0x880D0D14);
					RenderUtil.text(g, s.getName(), x + 4, ry + 3, 0xFFC8C8C8);
					RenderUtil.rect(g, x + w - 40, ry + 3, 10, 7, cs.rgb());
				}));
			}
			y += SET_ROW + 1;
		}
		// строка бинда
		int by = y;
		String keyLabel = m.getKeybind() > 0 ? KeyManager.keyName(m.getKeybind()) : "—";
		rows.add(new SettingRow(x, by, w, SET_ROW, () -> listeningBind = m, null, (g, mm) -> {
			RenderUtil.rect(g, x, by, w, SET_ROW, 0x880D0D14);
			RenderUtil.text(g, "Клавиша", x + 4, by + 3, 0xFFC8C8C8);
			RenderUtil.textRight(g, listeningBind == m ? "..." : keyLabel, x + w - 4, by + 3, 0xFFB388FF);
		}));
		return rows;
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
			searchBox.setFocused(true);
			return searchBox.mouseClicked(event, doubleClick);
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
		double mx = event.x(), my = event.y();
		if (dragArea != null) {
			dragArea.drag().accept(mx, my);
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
