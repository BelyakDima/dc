package dev.darkness.client.mixin;

import dev.darkness.client.util.CustomizeManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	@Inject(method = "renderBackground", at = @At("RETURN"))
	private void darkness$customTitleBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		CustomizeManager.renderTitleBackground(guiGraphics, guiGraphics.guiWidth(), guiGraphics.guiHeight());
	}
}
