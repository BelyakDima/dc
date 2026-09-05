package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@Inject(method = "setupFog", at = @At("RETURN"))
	private void darkness$customFogColor(Camera camera, int renderDistance, DeltaTracker deltaTracker,
										 float darkenWorldAmount, ClientLevel level,
										 CallbackInfoReturnable<Vector4f> cir) {
		var mm = DarknessClient.getModuleManager();
		if (mm == null) return;
		var m = mm.byName("WorldCustomizer");
		if (m == null || !m.isEnabled()) return;
		var enable = m.bool("Свой цвет тумана");
		if (enable == null || !enable.get()) return;
		int argb = m.color("Цвет тумана").rgb();
		Vector4f color = cir.getReturnValue();
		if (color != null) {
			color.set(((argb >> 16) & 0xFF) / 255.0f,
				((argb >> 8) & 0xFF) / 255.0f,
				(argb & 0xFF) / 255.0f,
				color.w);
		}
	}
}
