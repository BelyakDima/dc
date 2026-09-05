package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow
	protected abstract void setRotation(float yaw, float pitch);

	@Inject(method = "setup", at = @At("RETURN"))
	private void darkness$freeLook(Level level, Entity entity, boolean detached, boolean mirrored,
								   float partialTick, CallbackInfo ci) {
		if (DarknessClient.isFreeLookActive()) {
			this.setRotation(DarknessClient.freeLookYaw(), DarknessClient.freeLookPitch());
		}
	}
}
