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

package com.wildfire.main;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.wildfire.main.cloud.CloudSync;
import com.wildfire.main.config.ClientConfig;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.contributors.Contributors;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.WildfireSync;
import com.wildfire.render.debug.GenderDebugHudEntry;
import com.wildfire.render.debug.PhysicsDebugHudEntry;
import com.wildfire.resources.GenderArmorResourceManager;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class WildfireGenderClient implements ClientModInitializer {
    private static final Executor LOAD_EXECUTOR = Util.ioPool().forName("wildfire_gender$loadPlayerData");

    public static final LoadingCache<UUID, PlayerConfig> CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(15))
        .build(CacheLoader.from(key -> {
            var config = new PlayerConfig(key);
            // markForSync being true will only ever do anything for the client player
            loadGenderInfo(config, true, false);
            return config;
        }));

    @Override
    public void onInitializeClient() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        WildfireHelper.tryRename(configDir.resolve("WildfireGender"), configDir.resolve(Configuration.CONFIG_DIR));
        WildfireHelper.tryRename(configDir.resolve("wildfire_gender.json"), configDir.resolve("female_gender_mod.json"));

        ClientConfig.INSTANCE.load();
        WildfireSounds.register();
        WildfireSync.registerClient();
        WildfireEventHandler.registerClientEvents();
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(GenderArmorResourceManager.ID, GenderArmorResourceManager.INSTANCE);
        DebugScreenEntries.register(GenderDebugHudEntry.SELF, new GenderDebugHudEntry(true));
        DebugScreenEntries.register(GenderDebugHudEntry.OTHER, new GenderDebugHudEntry(false));
        // only register this in dev env, as this likely isn't going to be very useful anywhere else.
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            DebugScreenEntries.register(PhysicsDebugHudEntry.ID, new PhysicsDebugHudEntry());
        }
        WildfireCommand.init();
    }

    public static @Nullable PlayerConfig getPlayerById(UUID id) {
        return WildfireGenderClient.CACHE.getIfPresent(id);
    }

    public static PlayerConfig getOrAddPlayerById(UUID id) {
        return WildfireGenderClient.CACHE.getUnchecked(id);
    }

    /// @apiNote Use [#getOrAddPlayerById(UUID)] or [#CACHE] instead
    @ApiStatus.Internal
    public static CompletableFuture<PlayerConfig> loadGenderInfo(final PlayerConfig player, final boolean markForSync, final boolean bypassQueue) {
        return CompletableFuture.supplyAsync(() -> {
            var uuid = player.uuid;
            if(player.hasLocalConfig()) {
                player.loadFromDisk(markForSync);
            } else if(player.syncStatus == PlayerConfig.SyncStatus.UNKNOWN) {
                JsonObject data;
                try {
                    var future = bypassQueue ? CloudSync.getProfile(uuid) : CloudSync.queueFetch(uuid);
                    data = future.join();
                } catch(Exception e) {
                    WildfireGender.LOGGER.error("Failed to fetch profile from sync server", e);
                    throw e;
                }
                // make sure the server we're connected to hasn't provided player data while we were fetching data from
                // the sync server
                if(data != null && player.syncStatus == PlayerConfig.SyncStatus.UNKNOWN) {
                    player.updateFromJson(data);
                    if(markForSync) {
                        player.needsSync = true;
                    }
                }
            }
            return player;
        }, LOAD_EXECUTOR);
    }

    public static @Nullable Component getNametag(UUID uuid) {
        var clientPlayer = Minecraft.getInstance().player;
        if(ClientConfig.INSTANCE.get(ClientConfig.HIDE_OWN_CONTRIBUTOR_TAG) && clientPlayer != null && uuid.equals(clientPlayer.getUUID())) {
            return null;
        }

        return Contributors.getNametag(uuid);
    }
}
