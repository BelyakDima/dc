package dev.darkness.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.darkness.client.DarknessClient;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
	private void darkness$noHurtCam(PoseStack poseStack, float partialTick, CallbackInfo ci) {
		if (DarknessClient.shouldCancelHandBob()) {
			ci.cancel();
			return;
		}
		var m = DarknessClient.getModuleManager();
		if (m != null) {
			var noHurtCam = m.byName("NoHurtCam");
			if (noHurtCam != null && noHurtCam.isEnabled()) {
				ci.cancel();
			}
		}
	}

	@Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
	private void darkness$noHandBob(PoseStack poseStack, float partialTick, CallbackInfo ci) {
		if (DarknessClient.shouldCancelHandBob()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderItemInHand", at = @At("HEAD"))
	private void darkness$handRenderStart(float partialTick, boolean sleeping, org.joml.Matrix4f pose, CallbackInfo ci) {
		DarknessClient.setHandRendering(true);
	}

	@Inject(method = "renderItemInHand", at = @At("RETURN"))
	private void darkness$handRenderEnd(float partialTick, boolean sleeping, org.joml.Matrix4f pose, CallbackInfo ci) {
		DarknessClient.setHandRendering(false);
	}
}
