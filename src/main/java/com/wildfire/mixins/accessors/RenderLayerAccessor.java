package com.wildfire.mixins.accessors;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface RenderLayerAccessor {
	@Invoker
	RenderLayer callGetRenderLayer(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline);
}
