/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wildfire.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.wildfire.events.ArmorStatsTooltipEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
@Environment(EnvType.CLIENT)
abstract class ItemStackMixin {
	@Shadow public abstract Item getItem();

	@Shadow
	private static boolean isSectionVisible(int flags, ItemStack.TooltipSection tooltipSection) {
		throw new UnsupportedOperationException();
	}

	@Shadow protected abstract int getHideFlags();

	// @Local isn't playing nice, so instead do this ourselves with a @Share
	@WrapOperation(
			method = "getTooltip",
			at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false)
	)
	public <E> ArrayList<E> wildfiregender$grabList(
			Operation<ArrayList<E>> original,
			@Share(namespace = "wildfiregender", value = "lineList") LocalRef<ArrayList<E>> ref
	) {
		var list = original.call();
		ref.set(list);
		return list;
	}

	@Inject(
			method = "getTooltip",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1)
	)
	public void wildfiregender$armorStats(
			@Nullable PlayerEntity player,
			TooltipContext context,
			CallbackInfoReturnable<List<Text>> cir,
			@Share(namespace = "wildfiregender", value = "lineList") LocalRef<ArrayList<Text>> ref
	) {
		if(!isSectionVisible(getHideFlags(), ItemStack.TooltipSection.MODIFIERS)) return;
		if(this.getItem() instanceof ArmorItem) {
			ArmorStatsTooltipEvent.EVENT.invoker().appendTooltips((ItemStack)(Object)this, ref.get()::add, player);
		}
	}
}
