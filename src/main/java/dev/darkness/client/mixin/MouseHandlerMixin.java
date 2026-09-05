package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Shadow
	private double accumulatedDX;

	@Shadow
	private double accumulatedDY;

	@Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
	private void darkness$freeLookTurn(double partialTick, CallbackInfo ci) {
		if (DarknessClient.suppressPlayerTurn()) {
			// сбрасываем накопленные дельты, чтобы при отпускании не было рывка
			this.accumulatedDX = 0;
			this.accumulatedDY = 0;
			ci.cancel();
		}
	}
}
