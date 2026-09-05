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

	private record Area(int x, int y, int w, int h, Runnable click, BiConsumer<Double, Double> drag) {
	}

	private final List<Area> areas = new ArrayList<>();
	private Area dragArea = null;
	private double lastMx, lastMy;

	private static final int PANEL_X = 16, PANEL_Y = 40, PANEL_W = 210, ROW = 20;

	public ClickGuiScreen() {
		super(Component.literal("Darkness Client"));
	}

	@Override
	protected void init() {
		super.init();
		searchBox = new EditBox(this.font, PANEL_X, 18, 120, 14, Component.literal("Поиск"));
		searchBox.setMaxLength(24);
		searchBox.setHint(Component.literal("Поиск..."));
		searchBox.setCanLoseFocus(true);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		areas.clear();
		int w = this.width, h = this.height;
		RenderUtil.rect(g, 0, 0, w, h, 0x8806060C);

		RenderUtil.text(g, DarknessClient.MOD_NAME, 16, 6, 0xFFB388FF);
		String sub = "v" + DarknessClient.VERSION + " §8| " + DarknessClient.getModuleManager().getEnabledModules().size() + " вкл";
		RenderUtil.text(g, sub, 16 + this.font.width(DarknessClient.MOD_NAME) + 6, 6, 0xFF9E9E9E);

		// category tabs
		int catY = PANEL_Y;
		for (Category c : Category.values()) {
			boolean active = c == current && searchBox.getValue().isBlank();
			int bg = active ? RenderUtil.withAlpha(c.getColor(), 230) : 0xD90D0D14;
			RenderUtil.rect(g, 16, catY, 96, 18, bg);
			RenderUtil.outline(g, 16, catY, 96, 18, active ? 0xFFFFFFFF : 0x37FFFFFF);
			RenderUtil.textCentered(g, c.getDisplayName(), 16 + 48, catY + 5, active ? 0xFFFFFFFF : 0xFFD0D0D0);
			int cy = catY;
			registerArea(16, cy, 96, 18, () -> {
				current = c;
				expanded = null;
				scroll = 0;
				searchBox.setValue("");
			}, null);
			catY += 20;
		}

		// module list
		List<Module> modules = visibleModules();
		int px = PANEL_X + 104, py = PANEL_Y;
		int rowY = py;
		int totalH = 0;
		for (Module m : modules) totalH += rowHeight(m);
		scroll = Math.max(0, Math.min(scroll, Math.max(0, totalH - (this.height - py - 60))));

		int clipBottom = this.height - 46;
		int skipped = 0;
		for (Module m : modules) {
			int rh = rowHeight(m);
			if (skipped + rh <= scroll) {
				skipped += rh;
				continue;
			}
			int offset = skipped - (int) scroll;
			skipped += rh;
			if (rowY + (offset) > clipBottom) break;
			int yy = py + offset;
			boolean on = m.isEnabled();
			RenderUtil.rect(g, px, yy, PANEL_W, 18, on ? RenderUtil.withAlpha(current.getColor(), 60) : 0xB30D0D14);
			RenderUtil.outline(g, px, yy, PANEL_W, 18, on ? current.getColor() : 0x30FFFFFF);
			String label = m.getName();
			RenderUtil.text(g, label, px + 6, yy + 5, on ? 0xFFFFFFFF : 0xFFC8C8C8);
			String info = m.getDisplayInfo();
			if (info != null) {
				RenderUtil.textRight(g, info, px + PANEL_W - (m == expanded ? 66 : 8), yy + 5, 0xFF9E9E9E);
			}
			if (m == expanded) {
				RenderUtil.textRight(g, "▾", px + PANEL_W - 6, yy + 5, 0xFF9E9E9E);
			}
			int my = yy;
			Module mm = m;
			registerArea(px, my, PANEL_W, 18, () -> {
				expanded = expanded == mm ? null : mm;
				listeningBind = null;
			}, null);

			// toggle pill on the right
			int pillX = px + PANEL_W - 26, pillY = my + 6;
			RenderUtil.rect(g, pillX, pillY, 18, 6, on ? current.getColor() : 0xFF404040);
			RenderUtil.rect(g, on ? pillX + 12 : pillX, pillY - 1, 6, 8, 0xFFE0E0E0);
			registerArea(pillX, pillY - 2, 18, 10, mm::toggle, null);

			if (m == expanded) {
				int sy = yy + 20;
				for (SettingRow row : settingRows(m, px + 4, sy)) {
					if (row.y() > clipBottom) break;
					row.renderer().render(g, mouseX);
					registerArea(row.x(), row.y(), row.w(), row.h(), row.click(), row.drag());
				}
			}
			rowY += rh;
		}

		// bottom action buttons
		int bx = 16, by = this.height - 24;
		actionButton(g, bx, by, 96, 18, "HUD-редактор", () -> minecraft.setScreen(new HudEditorScreen()));
		actionButton(g, bx + 100, by, 96, 18, "Аккаунты", () -> minecraft.setScreen(new AccountsScreen()));
		actionButton(g, bx + 200, by, 110, 18, "Сохранить конфиг", () -> {
			DarknessClient.getConfigManager().save();
			DarknessClient.getNotifications().show("§aКонфиг сохранён");
		});

		if (listeningBind != null) {
			String hint = "Нажмите клавишу для " + listeningBind.getName() + " (Esc — убрать)";
			RenderUtil.rect(g, w / 2 - this.font.width(hint) / 2 - 6, h - 60, this.font.width(hint) + 12, 16, 0xE60D0D14);
			RenderUtil.textCentered(g, hint, w / 2, h - 56, 0xFFB388FF);
		}

		searchBox.render(g, mouseX, mouseY, partialTick);
	}

	private void actionButton(GuiGraphics g, int x, int y, int w, int h, String label, Runnable action) {
		RenderUtil.rect(g, x, y, w, h, 0xB3161622);
		RenderUtil.outline(g, x, y, w, h, 0x40FFFFFF);
		RenderUtil.textCentered(g, label, x + w / 2, y + 5, 0xFFE0E0E0);
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
		if (m != expanded) return 20;
		return 20 + m.getSettings().size() * 18 + 20; // + bind row + padding
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
				rows.add(new SettingRow(x, ry, w, 16,
					() -> bs.set(!bs.get()), null,
					(g, mm) -> {
						RenderUtil.rect(g, x, ry, w, 16, 0x880D0D14);
						RenderUtil.text(g, s.getName(), x + 4, ry + 4, 0xFFC8C8C8);
						RenderUtil.textRight(g, bs.get() ? "ВКЛ" : "ВЫКЛ", x + w - 4, ry + 4,
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
				rows.add(new SettingRow(x, ry, w, 16, null, apply, (g, mm) -> {
					RenderUtil.rect(g, x, fy, w, 16, 0x880D0D14);
					RenderUtil.text(g, s.getName(), x + 4, fy + 3, 0xFFC8C8C8);
					RenderUtil.textRight(g, ns.getDisplayValue(), x + w - 4, fy + 3, 0xFFB388FF);
					int barY = fy + 14;
					double t = (ns.get() - ns.getMin()) / (ns.getMax() - ns.getMin());
					RenderUtil.rect(g, fx, barY, fw, 2, 0x4DFFFFFF);
					RenderUtil.rect(g, fx, barY, (int) (fw * Math.max(0, Math.min(1, t))), 2, current.getColor());
				}));
			} else if (s instanceof dev.darkness.client.module.ModeSetting ms) {
				rows.add(new SettingRow(x, ry, w, 16, ms::cycle, null, (g, mm) -> {
					RenderUtil.rect(g, x, ry, w, 16, 0x880D0D14);
					RenderUtil.text(g, s.getName(), x + 4, ry + 4, 0xFFC8C8C8);
					RenderUtil.textRight(g, ms.get(), x + w - 4, ry + 4, 0xFFB388FF);
				}));
			} else if (s instanceof dev.darkness.client.module.ColorSetting cs) {
				rows.add(new SettingRow(x, ry, w, 16, () -> {
					int idx = 0;
					for (int i = 0; i < PALETTE.length; i++) {
						if ((PALETTE[i] & 0xFFFFFF) == (cs.get() & 0xFFFFFF)) {
							idx = i;
							break;
						}
					}
					cs.set(PALETTE[(idx + 1) % PALETTE.length]);
				}, null, (g, mm) -> {
					RenderUtil.rect(g, x, ry, w, 16, 0x880D0D14);
					RenderUtil.text(g, s.getName(), x + 4, ry + 4, 0xFFC8C8C8);
					RenderUtil.rect(g, x + w - 40, ry + 4, 10, 8, cs.rgb());
					RenderUtil.textRight(g, "", x + w - 4, ry + 4, 0xFFFFFFFF);
				}));
			}
			y += 18;
		}
		// bind row
		int by = y;
		String keyLabel = m.getKeybind() > 0 ? KeyManager.keyName(m.getKeybind()) : "—";
		rows.add(new SettingRow(x, by, w, 16, () -> listeningBind = m, null, (g, mm) -> {
			RenderUtil.rect(g, x, by, w, 16, 0x880D0D14);
			RenderUtil.text(g, "Клавиша", x + 4, by + 4, 0xFFC8C8C8);
			RenderUtil.textRight(g, listeningBind == m ? "..." : keyLabel, x + w - 4, by + 4, 0xFFB388FF);
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
			lastMx = mx;
			lastMy = my;
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
		lastMx = mx;
		lastMy = my;
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
