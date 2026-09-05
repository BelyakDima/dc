package dev.darkness.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.darkness.client.DarknessClient;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Inject(method = "renderHandsWithItems", at = @At("HEAD"))
	private void darkness$customHand(float partialTick, PoseStack poseStack,
									 net.minecraft.client.renderer.SubmitNodeCollector collector,
									 net.minecraft.client.player.LocalPlayer player, int light, CallbackInfo ci) {
		var mm = DarknessClient.getModuleManager();
		if (mm == null) return;
		var m = mm.byName("CustomHand");
		if (m == null || !m.isEnabled()) return;
		float dx = m.number("Смещение X").get().floatValue();
		float dy = m.number("Смещение Y").get().floatValue();
		float s = m.number("Масштаб").get().floatValue();
		if (dx != 0 || dy != 0) {
			poseStack.translate(dx, dy, 0);
		}
		if (s != 1.0f) {
			poseStack.scale(s, s, s);
		}
	}
}
