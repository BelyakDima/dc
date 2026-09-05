package dev.darkness.client.mixin;

import dev.darkness.client.DarknessClient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
	@Shadow
	protected AbstractContainerMenu menu;

	@Shadow
	protected Slot hoveredSlot;

	@Inject(method = "mouseScrolled(DDDD)Z", at = @At("HEAD"), cancellable = true)
	private void darkness$itemScroller(double mx, double my, double sx, double sy, CallbackInfoReturnable<Boolean> cir) {
		var m = DarknessClient.getModuleManager();
		if (m == null) return;
		var mod = m.byName("ItemScroller");
		if (mod == null || !mod.isEnabled()) return;
		var mc = net.minecraft.client.Minecraft.getInstance();
		if (mc.player == null || mc.gameMode == null || hoveredSlot == null) return;
		if (!hoveredSlot.hasItem()) return;

		if (sy > 0) {
			// скролл вверх — быстрый перенос стопки (как shift-клик)
			mc.gameMode.handleInventoryMouseClick(menu.containerId, hoveredSlot.index, 0,
				ClickType.QUICK_MOVE, mc.player);
			cir.setReturnValue(true);
		} else if (sy < 0) {
			// скролл вниз — поменять предмет с предметом в выбранном слоте хотбара
			mc.gameMode.handleInventoryMouseClick(menu.containerId, hoveredSlot.index,
				mc.player.getInventory().getSelectedSlot(), ClickType.SWAP, mc.player);
			cir.setReturnValue(true);
		}
	}
}
