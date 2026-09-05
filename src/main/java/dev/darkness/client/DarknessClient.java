package dev.darkness.client;

import dev.darkness.client.accounts.AccountManager;
import dev.darkness.client.command.CommandManager;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.ModuleManager;
import dev.darkness.client.module.modules.hud.*;
import dev.darkness.client.module.modules.optimize.*;
import dev.darkness.client.module.modules.util.AutoReconnectModule;
import dev.darkness.client.module.modules.util.AutoClickerModule;
import dev.darkness.client.module.modules.util.AutoRespawnModule;
import dev.darkness.client.module.modules.util.AutoToolModule;
import dev.darkness.client.module.modules.util.DeathCoordsModule;
import dev.darkness.client.module.modules.util.DropProtectModule;
import dev.darkness.client.module.modules.util.DurabilityAlertModule;
import dev.darkness.client.module.modules.util.ElytraSwapModule;
import dev.darkness.client.module.modules.util.ToggleSprintModule;
import dev.darkness.client.module.modules.util.TotemSwapModule;
import dev.darkness.client.module.modules.visual.*;
import dev.darkness.client.module.OverlayRenderer;
import dev.darkness.client.ui.ClickGuiScreen;
import dev.darkness.client.util.ConfigManager;
import dev.darkness.client.util.CpManager;
import dev.darkness.client.util.KeyManager;
import dev.darkness.client.util.Notifications;
import dev.darkness.client.util.RenderUtil;
import dev.darkness.client.util.TargetManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DarknessClient implements ClientModInitializer {
	public static final String MOD_ID = "darknessclient";
	public static final String MOD_NAME = "Darkness Client";
	public static final String VERSION = "1.3.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private static ModuleManager moduleManager;
	private static ConfigManager configManager;
	private static TargetManager targetManager;
	private static Notifications notifications;
	private static CommandManager commandManager;
	private static AccountManager accountManager;
	private static CpManager cpManager;
	private static final AtomicInteger particleCounter = new AtomicInteger();

	private static boolean prevLeft, prevRight;
	private static final boolean[] keyWasDown = new boolean[512];
	private static boolean handRendering = false;

	public static void setHandRendering(boolean v) {
		handRendering = v;
	}

	/** true в момент отрисовки рук, если CustomHand просит убрать тряску. */
	public static boolean shouldCancelHandBob() {
		if (!handRendering) return false;
		var m = moduleManager;
		if (m == null) return false;
		var ch = m.byName("CustomHand");
		return ch != null && ch.isEnabled() && ch.bool("Отключить тряску") != null && ch.bool("Отключить тряску").get();
	}

	@Override
	public void onInitializeClient() {
		moduleManager = new ModuleManager();
		targetManager = new TargetManager();
		notifications = new Notifications();
		commandManager = new CommandManager();
		cpManager = new CpManager();
		configManager = new ConfigManager();
		accountManager = new AccountManager(configManager.getDir());
		dev.darkness.client.util.CustomizeManager.init(configManager.getDir());

		registerModules();

		configManager.load();
		LOGGER.info("{} {} loaded: {} modules", MOD_NAME, VERSION, moduleManager.getModules().size());
	}

	private void registerModules() {
		// HUD
		moduleManager.register(new WatermarkModule());
		moduleManager.register(new ArrayListModule());
		moduleManager.register(new KeystrokesModule());
		moduleManager.register(new FpsModule());
		moduleManager.register(new PingModule());
		moduleManager.register(new CpsModule());
		moduleManager.register(new CoordinatesModule());
		moduleManager.register(new ArmorHudModule());
		moduleManager.register(new PotionHudModule());
		moduleManager.register(new TargetHudModule());
		moduleManager.register(new ComboModule());
		moduleManager.register(new ReachDisplayModule());
		moduleManager.register(new RamModule());
		moduleManager.register(new ServerInfoModule());
		moduleManager.register(new InventoryHudModule());
		moduleManager.register(new ClockModule());
		moduleManager.register(new SpeedometerModule());
		// Visuals
		moduleManager.register(new CrosshairModule());
		moduleManager.register(new DamageNumbersModule());
		moduleManager.register(new HitParticlesModule());
		moduleManager.register(new TrajectoriesModule());
		moduleManager.register(new TracersModule());
		moduleManager.register(new EspModule());
		moduleManager.register(new ZoomModule());
		moduleManager.register(new FullBrightModule());
		moduleManager.register(new CustomHandModule());
		moduleManager.register(new WorldCustomizerModule());
		moduleManager.register(new TimeChangerModule());
		moduleManager.register(new NoFovModule());
		moduleManager.register(new NoViewBobModule());
		// Optimization
		moduleManager.register(new NoRainModule());
		moduleManager.register(new NoHurtCamModule());
		moduleManager.register(new NoEntityShadowsModule());
		moduleManager.register(new ParticleLimitModule());
		moduleManager.register(new FpsBoostModule());
		moduleManager.register(new NoFireOverlayModule());
		moduleManager.register(new NoWaterOverlayModule());
		moduleManager.register(new NoBlockOverlayModule());
		moduleManager.register(new NoItemActivationModule());
		// Utility
		moduleManager.register(new AutoReconnectModule());
		moduleManager.register(new ElytraSwapModule());
		moduleManager.register(new TotemSwapModule());
		moduleManager.register(new ToggleSprintModule());
		moduleManager.register(new AutoRespawnModule());
		moduleManager.register(new AutoToolModule());
		moduleManager.register(new AutoClickerModule());
		moduleManager.register(new FuntimeHelperModule());
		moduleManager.register(new ScoreboardHideModule());
		moduleManager.register(new AntiNauseaModule());
		moduleManager.register(new DropProtectModule());
		moduleManager.register(new DurabilityAlertModule());
		moduleManager.register(new DeathCoordsModule());
		moduleManager.register(new FreeLookModule());
		moduleManager.register(new AspectRatioModule());
		moduleManager.register(new HitBoxesModule());
		moduleManager.register(new HitMarkerModule());
		moduleManager.register(new HurtDirectionModule());
		moduleManager.register(new LowHPOverlayModule());
		moduleManager.register(new AttackCooldownModule());
		moduleManager.register(new TotemCounterModule());
		moduleManager.register(new CompassModule());
		moduleManager.register(new BiomeHudModule());
		moduleManager.register(new PlayerCountModule());
		moduleManager.register(new ArrowCountModule());
		moduleManager.register(new SessionTimerModule());

		// defaults: показать базовый HUD
		byDefault("Watermark");
		byDefault("ArrayList");
		byDefault("FPS");
		byDefault("CPS");
		byDefault("Ping");
		byDefault("Coordinates");
		byDefault("Keystrokes");
		byDefault("Zoom");
		byDefault("ToggleSprint");
	}

	private void byDefault(String name) {
		Module m = moduleManager.byName(name);
		if (m != null && !m.getName().equals("ClickGUI")) m.setEnabled(true);
	}

	// ---- FreeLook ----
	private static boolean freeLookActive = false;
	private static float freeYaw, freePitch;
	private static double flLastX = -1, flLastY = -1;

	public static boolean isFreeLookActive() {
		return freeLookActive;
	}

	public static float freeLookYaw() {
		return freeYaw;
	}

	public static float freeLookPitch() {
		return freePitch;
	}

	/** Пока FreeLook активен — ванильный поворот игрока отменяется (MouseHandlerMixin). */
	public static boolean suppressPlayerTurn() {
		return freeLookActive;
	}

	/** Обновление свободной камеры по дельтам мыши (вызывается из FreeLookModule.onTick). */
	public static void tickFreeLook(boolean held, int keybind) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.mouseHandler == null) {
			freeLookActive = false;
			flLastX = flLastY = -1;
			return;
		}
		if (held && !mc.options.hideGui) {
			double mx = mc.mouseHandler.xpos();
			double my = mc.mouseHandler.ypos();
			if (!freeLookActive) {
				freeLookActive = true;
				freeYaw = mc.player.getYRot();
				freePitch = mc.player.getXRot();
				flLastX = mx;
				flLastY = my;
				return;
			}
			if (flLastX < 0) {
				flLastX = mx;
				flLastY = my;
				return;
			}
			double dx = mx - flLastX;
			double dy = my - flLastY;
			flLastX = mx;
			flLastY = my;
			float sens = (float) (mc.options.sensitivity().get() * 0.6 + 0.4);
			freeYaw -= (float) (dx * sens * 0.4);
			freePitch -= (float) (dy * sens * 0.4);
			freePitch = Math.max(-90f, Math.min(90f, freePitch));
			freeYaw = wrapDegrees(freeYaw);
		} else if (freeLookActive) {
			freeLookActive = false;
			flLastX = flLastY = -1;
		}
	}

	private static float wrapDegrees(float deg) {
		float d = deg % 360f;
		if (d >= 180f) d -= 360f;
		if (d < -180f) d += 360f;
		return d;
	}

	public static void onTick() {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) return;
		particleCounter.set(0);

		configManager.tick();
		targetManager.tick();
		targetManager.tickDamageNumbers();
		moduleManager.tickAll();

		// CPS edge detection
		boolean left = mc.mouseHandler != null && mc.mouseHandler.isLeftPressed();
		boolean right = mc.mouseHandler != null && mc.mouseHandler.isRightPressed();
		if (left && !prevLeft) cpManager.leftDown();
		if (!left && prevLeft) cpManager.leftUp();
		if (right && !prevRight) cpManager.rightDown();
		if (!right && prevRight) cpManager.rightUp();
		prevLeft = left;
		prevRight = right;

		// keybinds: event-based dispatch (see KeyboardHandlerMixin / onKeyPressed)
	}

	/** Called from KeyboardHandlerMixin on every key event (press & release). */
	public static void onKeyPressed(int key) {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null || mc.getWindow() == null || key < 0 || key >= keyWasDown.length) return;
		boolean down;
		try {
			down = com.mojang.blaze3d.platform.InputConstants.isKeyDown(mc.getWindow(), key);
		} catch (Exception e) {
			return;
		}
		boolean wasDown = keyWasDown[key];
		keyWasDown[key] = down;
		if (!down || wasDown) return; // only fresh presses

		if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			if (mc.screen == null) {
				mc.setScreen(new ClickGuiScreen());
			} else if (mc.screen instanceof ClickGuiScreen gui && !gui.isListeningBind()) {
				mc.setScreen(null);
			}
			return;
		}
		if (mc.screen != null) {
			// при открытом экране срабатывают только «действия» (свапы), если экран не ловит текст
			if (screenCapturesTyping(mc.screen)) return;
			for (Module m : moduleManager.getModules()) {
				if (m.getKeybind() == key && m.isActionOnly()) {
					m.onKeyPressed();
				}
			}
			return;
		}
		for (Module m : moduleManager.getModules()) {
			if (m.getKeybind() == key && !(m instanceof ZoomModule)) {
				m.onKeyPressed();
			}
		}
	}

	/** Экраны, где клавиши — это ввод текста (чат, поиск, поля аккаунтов). */
	private static boolean screenCapturesTyping(net.minecraft.client.gui.screens.Screen screen) {
		if (screen instanceof net.minecraft.client.gui.screens.ChatScreen) return true;
		if (screen instanceof ClickGuiScreen gui) return gui.isSearchFocused() || gui.isListeningBind();
		if (screen instanceof dev.darkness.client.ui.AccountsScreen acc) return acc.isNickFocused();
		return false;
	}

	/** Клики по собственному инвентарю (для свапов элитр/тотема). */
	public static void minecraftClick(int menuSlot) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.gameMode == null) return;
		mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, menuSlot, 0,
			net.minecraft.world.inventory.ClickType.PICKUP, mc.player);
	}

	public static void onHudRender(GuiGraphics g, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options == null || mc.options.hideGui) return;

		// FreeLook: обновляем дельты мыши каждый кадр
		var fl = moduleManager.byName("FreeLook");
		if (fl != null && fl.isEnabled()) {
			boolean held = fl.getKeybind() > 0 && KeyManager.isDown(fl.getKeybind());
			tickFreeLook(held && mc.screen == null, fl.getKeybind());
		}

		RenderUtil.updateProjection(partialTick);
		int sw = g.guiWidth();
		int sh = g.guiHeight();

		// world-projected overlays first (everything implementing OverlayRenderer)
		for (Module m : moduleManager.getEnabledModules()) {
			if (m instanceof OverlayRenderer or) {
				try {
					or.renderOverlay(g, sw, sh, partialTick);
				} catch (Exception e) {
					LOGGER.debug("overlay {} failed", m.getName(), e);
				}
			}
		}

		// crosshair (first person only)
		var crosshair = (CrosshairModule) moduleManager.byName("CustomCrosshair");
		if (crosshair != null && crosshair.isEnabled()
			&& mc.options.getCameraType().isFirstPerson()) {
			crosshair.render(g, sw, sh);
		}

		// HUD elements
		if (!(mc.screen instanceof dev.darkness.client.ui.HudEditorScreen)) {
			for (Module m : moduleManager.getEnabledModules()) {
				if (m instanceof dev.darkness.client.module.HudModule hud) {
					hud.renderScaled(g, sw, sh, partialTick);
				}
			}
		}

		notifications.render(g, sw, sh, partialTick);
	}

	public static void onDisconnectedFromServer() {
		var m = moduleManager.byName("AutoReconnect");
		if (m instanceof AutoReconnectModule ar) {
			ar.onDisconnect();
		}
		var z = moduleManager.byName("Zoom");
		if (z instanceof ZoomModule zoom) {
			zoom.reset();
		}
	}

	public static ModuleManager getModuleManager() {
		return moduleManager;
	}

	public static ConfigManager getConfigManager() {
		return configManager;
	}

	public static TargetManager getTargetManager() {
		return targetManager;
	}

	public static Notifications getNotifications() {
		return notifications;
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static AccountManager getAccountManager() {
		return accountManager;
	}

	public static CpManager getCpManager() {
		return cpManager;
	}

	public static AtomicInteger getParticleCounter() {
		return particleCounter;
	}
}
