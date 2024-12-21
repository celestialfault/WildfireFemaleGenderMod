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

package com.wildfire.resources;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.wildfire.api.IGenderArmor;
import com.wildfire.main.WildfireGender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class GenderArmorResourceManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private GenderArmorResourceManager() {
		super(new Gson(), "wildfire_gender_data");
	}

	public static final GenderArmorResourceManager INSTANCE = new GenderArmorResourceManager();
	private @Unmodifiable Map<Identifier, IGenderArmor> configs = Map.of();

	public static @Nullable IGenderArmor get(Identifier model) {
		return INSTANCE.configs.get(model);
	}

	public static Optional<IGenderArmor> get(ItemStack item) {
		return Optional.ofNullable(item)
				.map(v -> v.getItem() instanceof ArmorItem armorItem ? armorItem : null)
				.map(v -> v .getMaterial().getIdAsString())
				.map(Identifier::of)
				.map(GenderArmorResourceManager::get);
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(WildfireGender.MODID, "armor_data");
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		var built = new HashMap<Identifier, IGenderArmor>();
		//noinspection CodeBlock2Expr
		prepared.forEach((k, v) -> {
			built.put(k, IGenderArmor.CODEC.decode(JsonOps.INSTANCE, v).getOrThrow().getFirst());
		});
		this.configs = Collections.unmodifiableMap(built);
	}
}
