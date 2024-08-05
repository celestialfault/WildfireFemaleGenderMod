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

package com.wildfire.api;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

/**
 * Defines the texture data for a given armor piece when covering an entity's breasts
 */
public interface IBreastArmorTexture {
	// TODO: Vector2i might not be the best option here due to its mutable nature; would it be worth changing this
	//       to something else like int[], or possibly implementing a basic record version?

	/**
	 * The size of the texture file in pixels
	 *
	 * @implNote Defaults to {@code new Vector2i(64, 32)}
	 *
	 * @return A {@link Vector2i} indicating how large the texture file is
	 */
	default @NotNull Vector2i textureSize() {
		return new Vector2i(64, 32);
	}

	/**
	 * How large of an area from the texture sprite should be used for each breast
	 *
	 * @apiNote The X value of this should be halved from the total chest size to account for each breast side
	 *          rendering independently of each other.
	 *
	 * @implNote Defaults to {@code new Vector2i(4, 5)}
	 *
	 * @return A {@link Vector2i} indicating how large of an area should be grabbed from the texture sprite to display over
	 *         the wearer's breasts
	 */
	default @NotNull Vector2i dimensions() {
		return new Vector2i(4, 5);
	}

	/**
	 * Where the left breast should grab the texture from on the item's texture
	 *
	 * @implNote Defaults to {@code new Vector2i(16, 17)}
	 *
	 * @return A {@link Vector2i} indicating where the UV of this item should be
	 */
	default @NotNull Vector2i leftUv() {
		return new Vector2i(16, 17);
	}

	/**
	 * Where the right breast should grab the texture from on the item's texture
	 *
	 * @implNote Defaults to {@code new Vector2i(leftUv().x + dimensions().x, leftUv().y)}
	 *
	 * @return A {@link Vector2i} indicating where the UV of this item should be
	 */
	default @NotNull Vector2i rightUv() {
		Vector2i left = leftUv();
		return new Vector2i(left.x + dimensions().x, left.y);
	}
}
