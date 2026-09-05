package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.Camera;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ParticleStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	@Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
	private void darkness$limitParticles(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz, CallbackInfoReturnable<net.minecraft.client.particle.Particle> cir) {
		dev.darkness.client.module.ModuleManager mm = DarknessClient.getModuleManager();
		if (mm == null) return;
		var mod = mm.byName("ParticleLimit");
		if (mod == null || !mod.isEnabled()) return;
		var maxSetting = mod.number("Максимум");
		if (maxSetting == null) return;
		var counter = dev.darkness.client.DarknessClient.getParticleCounter();
		int max = (int) Math.round(maxSetting.get());
		if (counter.incrementAndGet() > max) {
			cir.setReturnValue(null);
		}
	}
}
