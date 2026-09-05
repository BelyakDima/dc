package dev.darkness.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.darkness.client.DarknessClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	private static final Minecraft MC = Minecraft.getInstance();
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

	@ModifyReturnValue(method = "getFov(Lnet/minecraft/client/Camera;FZ)F", at = @At("RETURN"))
	private float darkness$staticFov(float original, Camera camera, float partialTick, boolean useFovSetting) {
		var m = DarknessClient.getModuleManager();
		if (m == null || MC.player == null) return original;
		var noFov = m.byName("NoFov");
		if (noFov == null || !noFov.isEnabled()) return original;
		if (MC.gameRenderer.getMainCamera().entity() != MC.player) return original;
		return MC.options.fov().get().floatValue();
	}

	@ModifyReturnValue(method = "getProjectionMatrix(F)Lorg/joml/Matrix4f;", at = @At("RETURN"))
	private org.joml.Matrix4f darkness$aspectRatio(org.joml.Matrix4f original, float fov) {
		var m = DarknessClient.getModuleManager();
		if (m == null) return original;
		var mod = m.byName("AspectRatio");
		if (mod == null || !mod.isEnabled()) return original;
		String mode = mod.mode("Соотношение").get();
		int idx = mode.indexOf(':');
		if (idx <= 0) return original;
		try {
			float target = Float.parseFloat(mode.substring(0, idx)) / Float.parseFloat(mode.substring(idx + 1));
			double current = (double) MC.getWindow().getWidth() / MC.getWindow().getHeight();
			if (target <= 0 || current <= 0) return original;
			if (current < target) {
				original.scale((float) (target / current), 1f, 1f);
			} else if (current > target) {
				original.scale(1f, (float) (current / target), 1f);
			}
		} catch (NumberFormatException ignored) {
		}
		return original;
	}
}
