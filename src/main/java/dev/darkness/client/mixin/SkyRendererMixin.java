package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.SkyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.MoonPhase;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void darkness$customSky(ClientLevel level, float partialTick, Camera camera,
									SkyRenderState state, CallbackInfo ci) {
		var mm = DarknessClient.getModuleManager();
		if (mm == null) return;
		var m = mm.byName("WorldCustomizer");
		if (m != null && m.isEnabled()) {
			if (m.bool("Свой цвет неба") != null && m.bool("Свой цвет неба").get()) {
				state.skyColor = m.color("Цвет неба").rgb();
				state.sunriseAndSunsetColor = m.color("Цвет неба").rgb();
			}
		}
		var tc = mm.byName("TimeChanger");
		if (tc != null && tc.isEnabled()) {
			int target = ((dev.darkness.client.module.modules.visual.TimeChangerModule) tc).targetTime();
			float angle = (target / 24000f - 0.25f) * 360f;
			state.sunAngle = angle;
			state.moonAngle = angle;
			state.starAngle = angle;
		}
	}

	@Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true)
	private void darkness$hideCelestial(PoseStack poseStack, float f, float g, float h, MoonPhase moonPhase,
										float i, float j, CallbackInfo ci) {
		var mm = DarknessClient.getModuleManager();
		if (mm == null) return;
		var m = mm.byName("WorldCustomizer");
		if (m == null || !m.isEnabled()) return;
		var hide = m.bool("Скрыть солнце/луну/звёзды");
		if (hide != null && hide.get()) {
			ci.cancel();
		}
	}
}
