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

package com.wildfire.api.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wildfire.api.IBreastArmorTexture;
import com.wildfire.api.IGenderArmor;

import static com.wildfire.main.WildfireHelper.read;

/**
 * @see IGenderArmor
 */
public record GenderArmor(
		boolean coversBreasts,
		float physicsResistance,
		float tightness,
		boolean armorStandsCopySettings,
		boolean alwaysHidesBreasts,
		IBreastArmorTexture texture
) implements IGenderArmor {

	/**
	 * Default implementation used to represent armor types that lack any configuration
	 */
	public static final GenderArmor DEFAULT = new GenderArmor(true, 0.5f, 0f, false, false, DefaultBreastArmorTexture.DEFAULT);

	/**
	 * Default implementation used when the player {@link net.minecraft.item.ItemStack#isEmpty() isn't wearing any armor},
	 * or if the worn chestplate specifies that it doesn't cover the breasts.
	 */
	public static final GenderArmor EMPTY = new GenderArmor(false, 0f, 0f, false, false, DefaultBreastArmorTexture.DEFAULT);

	// TODO document the json syntax
	public static GenderArmor fromJson(JsonObject obj) {
		if(!read(obj, "covers_breasts", JsonElement::getAsBoolean).orElse(true)) {
			return EMPTY;
		}

		float resistance = read(obj, "resistance", JsonElement::getAsFloat).orElse(0.5f);
		float tightness = read(obj, "tightness", JsonElement::getAsFloat).orElse(0f);
		boolean armorStands = read(obj, "render_on_armor_stands", JsonElement::getAsBoolean).orElseGet(() -> resistance == 1f);
		boolean hidesBreasts = read(obj, "hides_breasts", JsonElement::getAsBoolean).orElse(false);
		IBreastArmorTexture tex = BreastArmorTexture.fromJson(obj);

		return new GenderArmor(true, resistance, tightness, armorStands, hidesBreasts, tex);
	}
}
