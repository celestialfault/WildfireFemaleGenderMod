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
import com.wildfire.main.entitydata.PlayerConfig;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public final class WildfireGenderServer {
    private WildfireGenderServer() {
    }

    public static final LoadingCache<UUID, PlayerConfig> CACHE = CacheBuilder.newBuilder().build(CacheLoader.from(PlayerConfig::new));

    public static @Nullable PlayerConfig getPlayerById(UUID id) {
        return CACHE.getIfPresent(id);
    }

    public static PlayerConfig getOrAddPlayerById(UUID id) {
        return CACHE.getUnchecked(id);
    }
}
