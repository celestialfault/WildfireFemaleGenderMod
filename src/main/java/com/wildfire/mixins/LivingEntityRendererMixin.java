/*
    Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
    Copyright (C) 2023 WildfireRomeo

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.wildfire.mixins;

import com.wildfire.render.GenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
	/*
	 * This is a terrible, horrible, no good, very bad bodge solution, which ideally we wouldn't need to do to begin with,
	 * but unfortunately with how this mod works, and with the changes made to entity renderers, we do.
	 *
	 * As of 24w33a (1.21.2), Mojang has replaced the entity reference provided to feature renderer instances with an
	 * _incredibly_ simplified render state object; while the player render state does provide a way for us to get
	 * the entity (by means of a provided entity ID), armor stands (and by extension all other entity types) lack
	 * this same entity ID property.
	 *
	 * As such, the only real way to actually get the rendered entity for non-player entities is to capture it in a
	 * variable further up the chain before the game packages everything up into the provided render state object.
	 *
	 * And on the same note, Mojang _also_ decided to not expose the tickDelta to the state, instead opting to just
	 * pre-calculate everything that uses it, meaning we'd _still_ need to capture this regardless of any entity IDs provided.
	 *
	 * TL;DR: if this feels like an ugly workaround to the problem, it's because it is (and one that appears to be
	 * required in some capacity either way).
	 */
	@Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
	public void wildfiregender$captureEntityRenderState(LivingEntity entity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
		if(!(entity instanceof PlayerEntity || entity instanceof ArmorStandEntity)) {
			return;
		}
		// A further note on the exact implementation details here: I would've liked to not use ThreadLocal to capture
		// these variables, but as far as I'm aware, Mixin doesn't have any reasonable way to inject new fields into
		// a class that can actually be accessed from outside the mixin in question, meaning this is the only
		// real way to get this in a sensible way.
		GenderLayer.ENTITY_ID.set(entity.getId());
		GenderLayer.PARTIAL_TICKS.set(tickDelta);
	}
}
