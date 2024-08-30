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

// TODO this could probably be removed if the mod api is removed as well; as-is, this is only here to ensure
//      that mods can't easily modify our static default vectors accidentally.
/**
 * Simplified immutable copy of a {@link org.joml.Vector2i Vector2i}
 */
public record Vec2i(int x, int y) {
	/**
	 * @param x The new X value for the returned {@link Vec2i}
	 * @return A new {@link Vec2i} instance with the provided X value, using the Y value of the current {@link Vec2i}
	 */
	public Vec2i withX(int x) {
		return new Vec2i(x, this.y);
	}

	/**
	 * Returns a new {@link Vec2i} with the sum of the provided values and this {@link Vec2i}
	 *
	 * @param x The value to add to this vector's X value
	 * @param y The value to add to this vector's Y value
	 *
	 * @return A new {@link Vec2i} with the added values
	 */
	public Vec2i add(int x, int y) {
		return new Vec2i(this.x + x, this.y + y);
	}
}
