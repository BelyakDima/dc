package dev.darkness.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.darkness.client.DarknessClient;
import dev.darkness.client.module.Module;
import dev.darkness.client.module.modules.visual.CustomHandModule;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

	// --- стиль анимации удара: кривая на прогрессе замаха ---
	@ModifyExpressionValue(
		method = "renderHandsWithItems",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttackAnim(F)F")
	)
	private float darkness$swingStyle(float original) {
		Module m = DarknessClient.getModuleManager() == null ? null : DarknessClient.getModuleManager().byName("CustomHand");
		if (m == null || !m.isEnabled()) return original;
		return CustomHandModule.applySwingStyle(m.mode("Анимация удара").get(), original);
	}

	// --- пер-рука трансформы (смещение/масштаб) ---
	@Inject(
		method = "renderArmWithItem",
		at = @At(value = "INVOKE", shift = At.Shift.AFTER,
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V")
	)
	private void darkness$perHandTransform(AbstractClientPlayer player, float partialTick, float xRot,
										   InteractionHand hand, float swingProgress,
										   ItemStack stack, float equipProgress,
										   PoseStack poseStack, SubmitNodeCollector collector, int light,
										   CallbackInfo ci) {
		Module m = DarknessClient.getModuleManager() == null ? null : DarknessClient.getModuleManager().byName("CustomHand");
		if (m == null || !m.isEnabled()) return;
		boolean mainHand = hand == InteractionHand.MAIN_HAND;
		HumanoidArm arm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
		boolean right = arm == HumanoidArm.RIGHT;

		double dxD = right ? m.number("Правая: X").get() : m.number("Левая: X").get();
		double dyD = right ? m.number("Правая: Y").get() : m.number("Левая: Y").get();
		double sD = right ? m.number("Правая: масштаб").get() : m.number("Левая: масштаб").get();
		float dx = (float) dxD;
		float dy = (float) dyD;
		float s = (float) sD;

		if (dx != 0f || dy != 0f) {
			poseStack.translate(dx, dy, 0f);
		}
		// масштаб вокруг точки крепления руки — рука уменьшается «на месте»,
		// а не уезжает к центру экрана
		if (s != 1f && s > 0f) {
			float px = right ? 0.56f : -0.56f;
			float py = -0.52f;
			float pz = -0.72f;
			poseStack.translate(px * (1 - s), py * (1 - s), pz * (1 - s));
			poseStack.scale(s, s, s);
		}
	}
}
