package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public abstract class WeatherEffectRendererMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void darkness$noRain(MultiBufferSource bufferSource, Vec3 cameraPos, WeatherRenderState state, CallbackInfo ci) {
		var mm = DarknessClient.getModuleManager();
		if (mm == null) return;
		var mod = mm.byName("NoRain");
		if (mod != null && mod.isEnabled()) {
			ci.cancel();
		}
	}

	@Inject(method = "tickRainParticles", at = @At("HEAD"), cancellable = true)
	private void darkness$noRainParticles(net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.client.Camera camera, int ticks, net.minecraft.server.level.ParticleStatus status, int range, CallbackInfo ci) {
		var mm = DarknessClient.getModuleManager();
		if (mm == null) return;
		var mod = mm.byName("NoRain");
		if (mod != null && mod.isEnabled()) {
			ci.cancel();
		}
	}
}
