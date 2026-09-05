package dev.darkness.client.module.modules.hud;

import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.BooleanSetting;
import dev.darkness.client.module.HudModule;
import dev.darkness.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TargetHudModule extends HudModule {
	private final BooleanSetting showFace;
	private final BooleanSetting showDistance;
	private final BooleanSetting showArmor;

	public TargetHudModule() {
		super("TargetHUD", "Цель: лицо, здоровье, броня, снаряжение, дистанция");
		showFace = new BooleanSetting("Лицо", "Показывать голову игрока", true);
		showDistance = new BooleanSetting("Дистанция", "Показывать расстояние до цели", true);
		showArmor = new BooleanSetting("Снаряжение", "Броня и предмет в руке цели", true);
		register(showFace, showDistance, showArmor);
	}

	@Override
	public void render(GuiGraphics g, int screenW, int screenH, float partialTick) {
		var tm = DarknessClient.getTargetManager();
		if (!tm.hasTarget() || !(tm.getTarget() instanceof LivingEntity target) || mc.player == null) {
			setSize(120, 40);
			return;
		}

		boolean showItems = showArmor.get();
		int w = 130, h = showItems ? 64 : 44;
		setSize(w, h);
		int x = getX(screenW), y = getY(screenH);
		float hurt = target.hurtTime > 0 ? target.hurtTime / 10f : 0;

		RenderUtil.rect(g, x, y, w, h, 0xE60D0D14);
		RenderUtil.outline(g, x, y, w, h, 0xFF7C4DFF);

		int faceX = x + 5, faceY = y + 5, faceSize = 24;
		int textX = faceX;
		if (showFace.get() && mc.getConnection() != null) {
			PlayerInfo info = null;
			for (PlayerInfo pi : mc.getConnection().getOnlinePlayers()) {
				if (pi.getProfile().id().equals(target.getUUID())) {
					info = pi;
					break;
				}
			}
			if (info != null) {
				PlayerFaceRenderer.draw(g, info.getSkin(), faceX, faceY, faceSize);
			}
			textX = faceX + faceSize + 5;
		}

		String name = target.getName().getString();
		if (name.length() > 12) name = name.substring(0, 12);
		RenderUtil.text(g, name, textX, y + 6, 0xFFF0F0F0);

		float health = target.getHealth();
		float maxHealth = target.getMaxHealth();
		int barY = y + 19;
		int barW = w - (textX - x) - 8;
		RenderUtil.rect(g, textX, barY, barW, 5, 0xFF000000);
		int hpColor = health / maxHealth > 0.5f ? 0xFF4CAF50 : health / maxHealth > 0.25f ? 0xFFFFC107 : 0xFFF44336;
		RenderUtil.rect(g, textX, barY, (int) (barW * Math.max(0, Math.min(1, health / maxHealth))), 5, hpColor);
		RenderUtil.text(g, String.format("%.1f ❤", health), textX, barY + 7, hpColor);

		if (showDistance.get() && mc.player != null) {
			String dist = String.format("%.1f м", mc.player.distanceTo(target));
			RenderUtil.textRight(g, dist, x + w - 4, y + 6, 0xFF9E9E9E);
		}

		int armor = target instanceof net.minecraft.world.entity.player.Player ? target.getArmorValue() : -1;
		if (armor >= 0) {
			RenderUtil.text(g, "⛨ " + armor, textX + mc.font.width(String.format("%.1f ❤", health)) + 8, barY + 7, 0xFF9E9E9E);
		}

		// снаряжение цели: шлем-нагрудник-штаны-ботинки + рука
		if (showItems) {
			int ix = x + 5, iy = y + 34;
			ItemStack[] gear = {
				target.getItemBySlot(EquipmentSlot.HEAD),
				target.getItemBySlot(EquipmentSlot.CHEST),
				target.getItemBySlot(EquipmentSlot.LEGS),
				target.getItemBySlot(EquipmentSlot.FEET),
				target.getMainHandItem(),
				target.getOffhandItem()
			};
			int i = 0;
			for (ItemStack st : gear) {
				if (st.isEmpty()) continue;
				g.renderItem(st, ix + i * 18, iy);
				float dmg = st.getMaxDamage() > 0 ? (float) (st.getMaxDamage() - st.getDamageValue()) / st.getMaxDamage() : 1f;
				if (st.isDamageableItem()) {
					int dbw = 16;
					RenderUtil.rect(g, ix + i * 18, iy + 16, dbw, 1, 0xFF000000);
					RenderUtil.rect(g, ix + i * 18, iy + 16, (int) (dbw * dmg), 1,
						dmg > 0.5f ? 0xFF66BB6A : dmg > 0.25f ? 0xFFFFC107 : 0xFFF44336);
				}
				i++;
			}
		}

		if (hurt > 0) {
			RenderUtil.rect(g, x, y, w, h, RenderUtil.withAlpha(0xFF3F1D2B, (int) (120 * hurt)));
		}
	}
}
