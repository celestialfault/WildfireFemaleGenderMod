package com.wildfire.mixins.accessors;

import net.minecraft.client.render.entity.model.AnimalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimalModel.class)
public interface AnimalModelAccessor {
	@Accessor
	float getInvertedChildBodyScale();

	@Accessor
	float getChildBodyYOffset();
}
