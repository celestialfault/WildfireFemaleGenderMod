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
import com.google.gson.JsonObject;
import com.wildfire.api.IGenderArmor;
import com.wildfire.api.impl.GenderArmor;
import com.wildfire.main.WildfireGender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Unmodifiable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public final class GenderArmorResourceManager implements IdentifiableResourceReloadListener {

	private GenderArmorResourceManager() {}

	public static final GenderArmorResourceManager INSTANCE = new GenderArmorResourceManager();
	public static final String PATH = "wildfire_gender_data";
	private static final Gson GSON = new Gson();
	private @Unmodifiable Map<Identifier, IGenderArmor> configs = Map.of();

	public static IGenderArmor get(Identifier itemId) {
		return INSTANCE.configs.getOrDefault(itemId, GenderArmor.DEFAULT);
	}

	public static IGenderArmor get(Item item) {
		return get(Registries.ITEM.getId(item));
	}

	public static boolean has(Identifier itemId) {
		return INSTANCE.configs.containsKey(itemId);
	}

	public static boolean has(Item item) {
		return has(Registries.ITEM.getId(item));
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(WildfireGender.MODID, "armor_data");
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager,
	                                      Profiler prepareProfiler, Profiler applyProfiler,
	                                      Executor prepareExecutor, Executor applyExecutor) {
		return CompletableFuture
				.supplyAsync(() -> prepareArmorMap(manager), prepareExecutor)
				.thenCompose(synchronizer::whenPrepared)
				.thenAcceptAsync(map -> this.configs = map, applyExecutor);
	}

	// this is static to ensure that this can't easily modify the current manager state
	private static Map<Identifier, IGenderArmor> prepareArmorMap(final ResourceManager manager) {
		final Map<Identifier, IGenderArmor> map = new HashMap<>();
		final Map<Identifier, List<Resource>> assets = manager.findAllResources(PATH,
				// filter to only load .json files which are not nested in any subdirectories
				id -> id.getPath().endsWith(".json") && StringUtils.countMatches(id.getPath(), '/') == 1);

		for(Map.Entry<Identifier, List<Resource>> entry : assets.entrySet()) {
			// take an identifier like 'minecraft:wildfire_gender_data/iron_chestplate.json' and extract just the 'iron_chestplate' name
			final String itemName = Arrays.asList(entry.getKey().toString().split("/")).getLast().replace(".json", "");
			final Identifier item = Identifier.of(entry.getKey().getNamespace(), itemName);
			// make a new json object to hold the merged values
			final JsonObject data = new JsonObject();

			for(Resource resource : entry.getValue()) {
				try(BufferedReader reader = resource.getReader()) {
					// load each resource pack's entry for the given item, replacing values from
					// resource packs lower in the reload order as needed
					GSON.fromJson(reader, JsonObject.class).asMap().forEach(data::add);
				} catch(IOException e) {
					throw new UncheckedIOException(e);
				}
			}

			map.put(item, GenderArmor.fromJson(data));
		}

		return Collections.unmodifiableMap(map);
	}
}
