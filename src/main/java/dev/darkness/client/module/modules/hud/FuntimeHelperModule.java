package dev.darkness.client.module.modules.hud;

import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ServerData;

import java.util.Locale;

/** Информационная панель для серверов Funtime: онлайн, пинг, координаты, сессия. */
public class FuntimeHelperModule extends HudModule {
	private final BooleanSetting showNether;
	private final BooleanSetting showSession;
	private long sessionStart = 0;

	public FuntimeHelperModule() {
		super("FuntimeHelper", "Панель для Funtime: онлайн, пинг, координаты, сессия");
		showNether = new BooleanSetting("Коорд. другого мира", "Показывать координаты ада/надмира", true);
		showSession = new BooleanSetting("Время сессии", "Сколько вы в игре", true);
		register(showNether, showSession);
	}

	private boolean isFuntime() {
		ServerData s = mc.getCurrentServer();
		return s != null && s.ip != null && s.ip.toLowerCase(Locale.ROOT).contains("funtime");
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		if (mc.player == null) {
			setSize(110, 12);
			return;
		}
		if (sessionStart == 0) sessionStart = System.currentTimeMillis();
		boolean ft = isFuntime();

		String ip = ft ? "Funtime" : (mc.getCurrentServer() != null ? mc.getCurrentServer().ip : "Одиночная игра");
		int online = -1;
		int ping = -1;
		if (mc.getConnection() != null) {
			online = mc.getConnection().getOnlinePlayers().size();
			for (var info : mc.getConnection().getOnlinePlayers()) {
				if (info.getProfile().id().equals(mc.player.getUUID())) {
					ping = info.getLatency();
					break;
				}
			}
		}

		String l1 = ft ? "§dFuntime §7helper" : "§7Сервер: §f" + truncate(ip, 18);
		String l2 = "Онлайн: §a" + (online >= 0 ? online : "—") + " §7Пинг: §f" + (ping >= 0 ? ping : "—");
		String l3 = String.format(Locale.ROOT, "%.0f %.0f %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ());

		int w = Math.max(mc.font.width(strip(l1)), Math.max(mc.font.width(strip(l2)), mc.font.width(strip(l3)))) + 10;
		int h = 12 + (showSession.get() ? 11 : 0) + (showNether.get() ? 11 : 0);
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		RenderUtil.rect(g, x, y, w, h, ft ? 0xE01A0A24 : 0xE00D0D14);
		RenderUtil.rect(g, x, y, w, 1, ft ? 0xFFE040FB : 0xFF7C4DFF);
		RenderUtil.text(g, l1, x + 5, y + 3, 0xFFFFFFFF);
		RenderUtil.text(g, l2, x + 5, y + 13, 0xFFE0E0E0);
		RenderUtil.text(g, "XYZ: " + l3, x + 5, y + 24, 0xFFE0E0E0);
		int yy = y + 35;
		if (showNether.get() && mc.level != null) {
			boolean nether = mc.level.dimension() == net.minecraft.world.level.Level.NETHER;
			String coords = nether
				? String.format(Locale.ROOT, "Надмир: %.0f %.0f", mc.player.getX() * 8, mc.player.getZ() * 8)
				: String.format(Locale.ROOT, "Ад: %.0f %.0f", mc.player.getX() / 8, mc.player.getZ() / 8);
			RenderUtil.text(g, coords, x + 5, yy, 0xFFB0B0B0);
			yy += 11;
		}
		if (showSession.get()) {
			long s = (System.currentTimeMillis() - sessionStart) / 1000;
			String t = String.format("Сессия: %02d:%02d:%02d", s / 3600, s / 60 % 60, s % 60);
			RenderUtil.text(g, t, x + 5, yy, 0xFFB0B0B0);
		}
	}

	@Override
	public void onTick() {
		if (mc.player == null && sessionStart != 0) sessionStart = 0;
	}

	private static String truncate(String s, int max) {
		return s.length() > max ? s.substring(0, max - 1) + "…" : s;
	}

	private static String strip(String s) {
		return s.replaceAll("§.", "");
	}
}
