package dev.darkness.client.mixin;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionInstance.class)
public interface OptionInstanceAccessor {
	@Accessor("value")
	Object darkness$getValue();

	@Accessor("value")
	void darkness$setValue(Object value);
}
