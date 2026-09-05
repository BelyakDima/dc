package dev.darkness.client.module.modules.util;

import dev.darkness.client.module.Category;
import dev.darkness.client.module.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class AutoToolModule extends Module {
	public AutoToolModule() {
		super("AutoTool", "Автовыбор лучшего инструмента при копании", Category.UTILITY);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null) return;
		if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return;
		BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos();
		BlockState state = mc.level.getBlockState(pos);
		if (state.isAir()) return;

		var inv = mc.player.getInventory();
		float best = stackSpeed(inv.getSelectedItem(), state);
		int bestSlot = inv.getSelectedSlot();
		for (int slot = 0; slot < 9; slot++) {
			float speed = stackSpeed(inv.getItem(slot), state);
			if (speed > best) {
				best = speed;
				bestSlot = slot;
			}
		}
		if (bestSlot != inv.getSelectedSlot()) {
			inv.setSelectedSlot(bestSlot);
		}
	}

	private static float stackSpeed(ItemStack stack, BlockState state) {
		Tool tool = stack.get(DataComponents.TOOL);
		return tool == null ? 1.0f : tool.getMiningSpeed(state);
	}
}
