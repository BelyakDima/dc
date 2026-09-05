package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
	private static boolean on(String name) {
		var mm = DarknessClient.getModuleManager();
		if (mm == null) return false;
		var m = mm.byName(name);
		return m != null && m.isEnabled();
	}

	@Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
	private static void darkness$noFire(com.mojang.blaze3d.vertex.PoseStack poseStack,
										net.minecraft.client.renderer.MultiBufferSource buffer,
										net.minecraft.client.renderer.texture.TextureAtlasSprite sprite, CallbackInfo ci) {
		if (on("NoFireOverlay")) ci.cancel();
	}

	@Inject(method = "renderWater", at = @At("HEAD"), cancellable = true)
	private static void darkness$noWater(net.minecraft.client.Minecraft minecraft,
										 com.mojang.blaze3d.vertex.PoseStack poseStack,
										 net.minecraft.client.renderer.MultiBufferSource buffer, CallbackInfo ci) {
		if (on("NoWaterOverlay")) ci.cancel();
	}

	@Inject(method = "renderTex", at = @At("HEAD"), cancellable = true)
	private static void darkness$noBlockOverlay(net.minecraft.client.renderer.texture.TextureAtlasSprite sprite,
												com.mojang.blaze3d.vertex.PoseStack poseStack,
												net.minecraft.client.renderer.MultiBufferSource buffer, CallbackInfo ci) {
		if (on("NoBlockOverlay")) ci.cancel();
	}

	@Inject(method = "renderItemActivationAnimation", at = @At("HEAD"), cancellable = true)
	private void darkness$noItemActivation(com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick,
										   net.minecraft.client.renderer.SubmitNodeCollector collector, CallbackInfo ci) {
		if (on("NoItemActivation")) ci.cancel();
	}
}
