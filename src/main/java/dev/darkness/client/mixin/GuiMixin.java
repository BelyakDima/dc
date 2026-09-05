package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Inject(method = "render", at = @At("TAIL"))
	private void darkness$onRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		DarknessClient.onHudRender(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void darkness$customCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		var m = DarknessClient.getModuleManager();
		if (m != null) {
			var crosshair = m.byName("CustomCrosshair");
			if (crosshair != null && crosshair.isEnabled()) {
				ci.cancel();
			}
		}
	}
}
