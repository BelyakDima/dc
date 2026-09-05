package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Inject(method = "renderBlockOutline", at = @At("HEAD"), cancellable = true)
	private void darkness$noBlockOutline(net.minecraft.client.renderer.MultiBufferSource.BufferSource buffer,
										 PoseStack poseStack, boolean wireframe,
										 net.minecraft.client.renderer.state.LevelRenderState state, CallbackInfo ci) {
		var m = DarknessClient.getModuleManager();
		if (m != null) {
			var mod = m.byName("NoBlockOutline");
			if (mod != null && mod.isEnabled()) {
				ci.cancel();
			}
		}
	}
}
