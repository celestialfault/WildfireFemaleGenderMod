package com.wildfire.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ModCompat {
	default boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	static @NotNull ItemStack getChestplate(LivingEntity entity) {
		Optional<ItemStack> trinkets = TrinketsCompat.INSTANCE.getChestplate(entity);
		return trinkets.orElseGet(() -> entity.getEquippedStack(EquipmentSlot.CHEST));
	}
}
