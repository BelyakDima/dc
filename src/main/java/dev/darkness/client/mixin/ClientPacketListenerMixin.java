package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
	private void darkness$onChat(String message, CallbackInfo ci) {
		if (message.startsWith(".")) {
			ci.cancel();
			var cm = DarknessClient.getCommandManager();
			if (cm != null) {
				cm.dispatch(message.substring(1));
			}
		}
	}
}
