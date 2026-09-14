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

package com.wildfire.api.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.wildfire.api.EntityCache;
import com.wildfire.common.entities.EntityConfig;
import com.wildfire.common.entities.EntityConfigHolder;
import java.time.Duration;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public class EntityCacheImpl<TYPE extends EntityConfigHolder<CONFIG>, CONFIG extends EntityConfig> implements EntityCache<TYPE, CONFIG> {
    private final LoadingCache<UUID, TYPE> cache;

    public EntityCacheImpl(final CacheLoader<UUID, TYPE> loader, final Codec<CONFIG> codec, final boolean autoExpire) {
        this.cache = Util.make(CacheBuilder.newBuilder(), builder -> {
            if(autoExpire) {
                builder.expireAfterAccess(Duration.ofMinutes(15));
            }
        }).build(loader);
    }

    public EntityCacheImpl(final BiFunction<UUID, CONFIG, TYPE> constructor, final Codec<CONFIG> codec, final boolean autoExpire) {
        final CacheLoader<UUID, TYPE> loader = CacheLoader.from(uuid -> {
            // the assumption here is that the provided codec can handle an empty object
            CONFIG newConfig = codec.parse(JsonOps.INSTANCE, JsonOps.INSTANCE.emptyMap()).getOrThrow();
            return constructor.apply(uuid, newConfig);
        });
        this(loader, codec, autoExpire);
    }

    @Override
    public @Nullable TYPE get(final UUID uuid) {
        return cache.getIfPresent(uuid);
    }

    @Override
    public TYPE getOrCreate(final UUID uuid) {
        return cache.getUnchecked(uuid);
    }

    @Override
    public void invalidate(final UUID uuid) {
        cache.invalidate(uuid);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public void invalidateIf(final Predicate<TYPE> predicate) {
        cache.asMap().values().removeIf(predicate);
    }

    public LoadingCache<UUID, TYPE> cache() {
        return cache;
    }
}
