package dev.darkness.client;

import dev.darkness.client.accounts.AccountManager;
import dev.darkness.client.command.CommandManager;
import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.ModuleManager;
import dev.darkness.client.module.modules.hud.*;
import dev.darkness.client.module.modules.optimize.*;
import dev.darkness.client.module.modules.util.AutoReconnectModule;
import dev.darkness.client.module.modules.visual.*;
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
	public static final String VERSION = "1.0.0";
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

	@Override
	public void onInitializeClient() {
		moduleManager = new ModuleManager();
		targetManager = new TargetManager();
		notifications = new Notifications();
		commandManager = new CommandManager();
		cpManager = new CpManager();
		configManager = new ConfigManager();
		accountManager = new AccountManager(configManager.getDir());

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
		// Visuals
		moduleManager.register(new CrosshairModule());
		moduleManager.register(new DamageNumbersModule());
		moduleManager.register(new HitParticlesModule());
		moduleManager.register(new TrajectoriesModule());
		moduleManager.register(new TracersModule());
		moduleManager.register(new EspModule());
		moduleManager.register(new ZoomModule());
		moduleManager.register(new FullBrightModule());
		// Optimization
		moduleManager.register(new NoRainModule());
		moduleManager.register(new NoHurtCamModule());
		moduleManager.register(new NoEntityShadowsModule());
		moduleManager.register(new ParticleLimitModule());
		moduleManager.register(new FpsBoostModule());
		// Utility
		moduleManager.register(new AutoReconnectModule());

		// defaults: показать базовый HUD
		byDefault("Watermark");
		byDefault("ArrayList");
		byDefault("FPS");
		byDefault("CPS");
		byDefault("Ping");
		byDefault("Coordinates");
		byDefault("Keystrokes");
		byDefault("Zoom");
	}

	private void byDefault(String name) {
		Module m = moduleManager.byName(name);
		if (m != null && !m.getName().equals("ClickGUI")) m.setEnabled(true);
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
		if (mc.screen != null) return;
		for (Module m : moduleManager.getModules()) {
			if (m.getKeybind() == key && !(m instanceof ZoomModule)) {
				m.toggle();
			}
		}
	}

	public static void onHudRender(GuiGraphics g, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options == null || mc.options.hideGui) return;

		RenderUtil.updateProjection(partialTick);
		int sw = g.guiWidth();
		int sh = g.guiHeight();

		// world-projected overlays first
		for (Module m : moduleManager.getEnabledModules()) {
			if (m instanceof DamageNumbersModule dm) dm.renderOverlay(g);
			else if (m instanceof HitParticlesModule hp) hp.renderOverlay(g);
			else if (m instanceof TrajectoriesModule tj) tj.renderOverlay(g);
			else if (m instanceof TracersModule tr) tr.renderOverlay(g, sw, sh);
			else if (m instanceof EspModule esp) esp.renderOverlay(g);
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
					hud.render(g, sw, sh, partialTick);
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
