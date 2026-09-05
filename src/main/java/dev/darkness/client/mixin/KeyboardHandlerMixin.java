package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	@Inject(method = "keyPress", at = @At("HEAD"))
	private void darkness$onKeyPress(long windowPointer, int action, KeyEvent event, CallbackInfo ci) {
		// action: 1 = нажатие, 0 = отпускание, 2 = автоповтор.
		// Реагируем только на чистое нажатие и берём код клавиши из KeyEvent.
		if (action == 1) {
			DarknessClient.onKeyPressed(event.key());
		}
	}
}
