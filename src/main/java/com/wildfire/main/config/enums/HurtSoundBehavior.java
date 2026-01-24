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

package com.wildfire.main.config.enums;

import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

import java.util.Locale;
import java.util.function.IntFunction;

public enum HurtSoundBehavior {
	OVERLAY,
	REPLACE,
	;

	public static final IntFunction<HurtSoundBehavior> BY_ID = ByIdMap.continuous(HurtSoundBehavior::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

	public HurtSoundBehavior next() {
		return BY_ID.apply(ordinal() + 1);
	}

	public Component getDisplayName() {
		return Component.translatable("wildfire_gender.hurt_sound_behavior." + name().toLowerCase(Locale.ROOT));
	}

	public Component getDescription() {
		return Component.empty()
			.append(Component.translatable("wildfire_gender.hurt_sound_behavior." + name().toLowerCase(Locale.ROOT) + ".description"))
			.append("\n\n")
			.append(Component.translatable("wildfire_gender.hurt_sound_behavior.client_only_disclaimer"));
	}
}
