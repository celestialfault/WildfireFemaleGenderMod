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

package com.wildfire.api.client;

import com.google.common.cache.CacheLoader;
import com.wildfire.api.EntityCache;
import com.wildfire.api.impl.EntityCacheImpl;
import com.wildfire.client.WildfireGenderClient;
import com.wildfire.common.entities.EntityConfig;
import com.wildfire.common.entities.EntityConfigHolder;
import com.wildfire.common.entities.armorstands.ArmorStandConfigHolder;
import com.wildfire.common.entities.avatars.AvatarConfigHolder;
import com.wildfire.common.entities.players.PlayerConfigHolder;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

/// Client-side API methods for interacting with the Female Gender Mod
public final class WildfireClientAPI {
    private WildfireClientAPI() {
    }

    private static final EntityCache<PlayerConfigHolder> PLAYERS;
    private static final EntityCache<ArmorStandConfigHolder> ARMOR_STANDS;
    private static final EntityCache<AvatarConfigHolder> AVATARS;

    static {
        final CacheLoader<UUID, PlayerConfigHolder> playerLoader = CacheLoader.from(uuid -> {
            var holder = new PlayerConfigHolder(uuid);
            WildfireGenderClient.loadGenderInfo(holder, true, false);
            return holder;
        });

        PLAYERS = new EntityCacheImpl<>(playerLoader, Duration.ofMinutes(15));
        ARMOR_STANDS = new EntityCacheImpl<>(CacheLoader.from(ArmorStandConfigHolder::new), Duration.ofMinutes(5));
        AVATARS = new EntityCacheImpl<>(CacheLoader.from(AvatarConfigHolder::new), Duration.ofMinutes(5));
    }

    @Nullable
    public static EntityConfigHolder<? extends EntityConfig> getConfig(LivingEntity entity) {
        return switch(entity) {
            case Player _ -> players().getOrCreate(entity);
            case Avatar _ -> avatars().getOrCreate(entity);
            case ArmorStand _ -> armorStands().getOrCreate(entity);
            default -> null;
        };
    }

    /// Returns the [EntityCache] supplying [PlayerConfigHolder] instances for client-side player entities
    public static EntityCache<PlayerConfigHolder> players() {
        return PLAYERS;
    }

    /// Returns the [EntityCache] supplying [AvatarConfigHolder] instances for client-side mannequin entities
    public static EntityCache<AvatarConfigHolder> avatars() {
        return AVATARS;
    }

    /// Returns the [EntityCache] supplying [ArmorStandConfigHolder] instances for client-side armor stand entities
    public static EntityCache<ArmorStandConfigHolder> armorStands() {
        return ARMOR_STANDS;
    }
}
