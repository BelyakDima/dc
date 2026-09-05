package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void darkness$onTick(CallbackInfo ci) {
		DarknessClient.onTick();
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
	private void darkness$onDisconnect(net.minecraft.client.gui.screens.Screen screen, boolean isTransferring, CallbackInfo ci) {
		DarknessClient.onDisconnectedFromServer();
	}
}
