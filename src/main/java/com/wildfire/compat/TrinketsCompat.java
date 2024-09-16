package com.wildfire.compat;

import com.wildfire.main.WildfireGender;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Optional;

public final class TrinketsCompat implements ModCompat {
	private TrinketsCompat() {}

	public static final TrinketsCompat INSTANCE = new TrinketsCompat();
	private static boolean COMPAT_ENABLED = true;

	private final boolean ACCESSORIES = isModLoaded("accessories");
	private final boolean TRINKETS = isModLoaded("trinkets");
	private final boolean VANITY_SLOTS = isModLoaded("vanityslots");

	private final String CONTAINER = VANITY_SLOTS ? "vanityslots:vanity" : "chest";
	private final String SLOT = VANITY_SLOTS ? "vanity_shirt" : "cosmetic";

	public Optional<ItemStack> getChestplate(LivingEntity entity) {
		if(!TRINKETS || !COMPAT_ENABLED) {
			return Optional.empty();
		}
		try {
			return getChestplateInternal(entity);
		} catch(Exception e) {
			WildfireGender.LOGGER.error("Failed to get cosmetic armor from Trinkets; disabling compatibility hook for this session.", e);
			COMPAT_ENABLED = false;
			return Optional.empty();
		}
	}

	private Optional<ItemStack> getChestplateInternal(LivingEntity entity) {
		return TrinketsApi.getTrinketComponent(entity)
				.map(TrinketComponent::getInventory)
				.flatMap(inv -> {
					if(ACCESSORIES) {
						return Optional.ofNullable(inv.get(CONTAINER))
								// wtf is this insanity that the accessories trinkets compatibility requires?
								.flatMap(container -> container.entrySet().stream()
										.filter(entry -> entry.getKey().equals(SLOT))
										.findFirst()
										.map(Map.Entry::getValue));
					} else {
						return Optional.ofNullable(inv.get(CONTAINER)).map(chest -> chest.get(SLOT));
					}
				})
				.map(slots -> slots.getStack(0))
				.filter(stack -> !stack.isEmpty());
	}
}
