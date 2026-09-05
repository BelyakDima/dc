package dev.darkness.client.ui;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.accounts.Account;
import dev.darkness.client.accounts.AccountManager;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AccountsScreen extends Screen {
	private EditBox nickBox;
	private final List<ClickZone> zones = new ArrayList<>();
	private double scroll;

	private record ClickZone(int x, int y, int w, int h, Runnable action) {
	}

	public AccountsScreen() {
		super(Component.literal("Аккаунты"));
	}

	@Override
	protected void init() {
		super.init();
		nickBox = new EditBox(this.font, 16, this.height - 46, 120, 16, Component.literal("Ник"));
		nickBox.setMaxLength(16);
		nickBox.setHint(Component.literal("Ник нового аккаунта"));
		nickBox.setCanLoseFocus(true);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		zones.clear();
		RenderUtil.rect(g, 0, 0, this.width, this.height, 0x8806060C);
		AccountManager am = DarknessClient.getAccountManager();

		RenderUtil.text(g, "Аккаунты", 16, 8, 0xFFB388FF);
		RenderUtil.text(g, "Текущий: §a" + am.getCurrentName(), 100, 8, 0xFFE0E0E0);

		int y = 28;
		List<Account> accounts = am.getAccounts();
		for (Account acc : accounts) {
			if (y > this.height - 70) break;
			RenderUtil.rect(g, 16, y, 220, 20, 0xB30D0D14);
			RenderUtil.outline(g, 16, y, 220, 20, 0x30FFFFFF);
			RenderUtil.text(g, acc.getName(), 22, y + 6, 0xFFF0F0F0);
			String type = acc.isOffline() ? "оффлайн" : "лицензия";
			RenderUtil.textRight(g, type, 16 + 70, y + 6, 0xFF9E9E9E);

			int loginX = 140, delX = 186;
			RenderUtil.rect(g, loginX, y + 3, 40, 14, 0xFF33691E);
			RenderUtil.textCentered(g, "Войти", loginX + 20, y + 7, 0xFFFFFFFF);
			RenderUtil.rect(g, delX, y + 3, 40, 14, 0xFF8E2430);
			RenderUtil.textCentered(g, "Удалить", delX + 20, y + 7, 0xFFFFFFFF);
			int ry = y;
			zones.add(new ClickZone(loginX, ry + 3, 40, 14, () -> {
				if (am.login(acc)) {
					DarknessClient.getNotifications().show("Вход: §a" + acc.getName());
				}
			}));
			zones.add(new ClickZone(delX, ry + 3, 40, 14, () -> {
				am.remove(acc);
				DarknessClient.getNotifications().show("Удалён: §c" + acc.getName());
			}));
			y += 22;
		}

		RenderUtil.rect(g, 16, this.height - 46, 120, 16, 0xB30D0D14);
		nickBox.render(g, mouseX, mouseY, partialTick);
		int addX = 142;
		RenderUtil.rect(g, addX, this.height - 46, 90, 16, 0xFF33691E);
		RenderUtil.textCentered(g, "Добавить", addX + 45, this.height - 42, 0xFFFFFFFF);
		zones.add(new ClickZone(addX, this.height - 46, 90, 16, this::addAccount));

		String hint = "Смена аккаунта действует до перезапуска. На серверах нужен переподключение.";
		RenderUtil.text(g, hint, 16, this.height - 62, 0xFF757575);
	}

	private void addAccount() {
		String nick = nickBox.getValue().trim();
		if (nick.isEmpty() || !nick.matches("[A-Za-z0-9_]{1,16}")) {
			DarknessClient.getNotifications().show("§cНик: 1-16 символов A-Z, 0-9, _");
			return;
		}
		Account acc = Account.offline(nick);
		DarknessClient.getAccountManager().add(acc);
		DarknessClient.getNotifications().show("Добавлен: §a" + nick);
		nickBox.setValue("");
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x(), my = event.y();
		if (mx >= nickBox.getX() && mx <= nickBox.getX() + nickBox.getWidth()
			&& my >= nickBox.getY() && my <= nickBox.getY() + nickBox.getHeight()) {
			setFocused(nickBox);
			nickBox.setFocused(true);
			return nickBox.mouseClicked(event, doubleClick);
		}
		for (int i = zones.size() - 1; i >= 0; i--) {
			ClickZone z = zones.get(i);
			if (mx >= z.x() && mx <= z.x() + z.w() && my >= z.y() && my <= z.y() + z.h()) {
				z.action().run();
				return true;
			}
		}
		nickBox.setFocused(false);
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (nickBox.isFocused() && event.key() == GLFW.GLFW_KEY_ENTER) {
			addAccount();
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
	}

	public boolean isNickFocused() {
		return nickBox != null && nickBox.isFocused();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
