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
		var m = DarknessClient.getModuleManager();
		if (m != null) {
			var noHurtCam = m.byName("NoHurtCam");
			if (noHurtCam != null && noHurtCam.isEnabled()) {
				ci.cancel();
			}
		}
	}
}
