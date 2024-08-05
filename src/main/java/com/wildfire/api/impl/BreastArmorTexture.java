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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.wildfire.api.IBreastArmorTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import static com.wildfire.main.WildfireHelper.read;

public record BreastArmorTexture(
		@NotNull Vector2i textureSize, @NotNull Vector2i leftUv, @NotNull Vector2i rightUv, @NotNull Vector2i dimensions
) implements IBreastArmorTexture {

	private static final Vector2i DEFAULT_TEXTURE_SIZE = new Vector2i(64, 32);
	private static final Vector2i DEFAULT_DIMENSIONS = new Vector2i(4, 5);
	private static final Vector2i DEFAULT_LEFT_UV = new Vector2i(16, 17);

	public static BreastArmorTexture fromJson(JsonObject obj) {
		final Vector2i textureSize = read(obj, "texture_size", BreastArmorTexture::getAsVec2i)
				.orElseGet(() -> new Vector2i(DEFAULT_TEXTURE_SIZE));
		final Vector2i dimensions = read(obj, "dimensions", BreastArmorTexture::getAsVec2i)
				.orElseGet(() -> new Vector2i(DEFAULT_DIMENSIONS));
		final Vector2i leftUv = read(obj, "uv", BreastArmorTexture::getAsVec2i)
				.orElseGet(() -> new Vector2i(DEFAULT_LEFT_UV));
		final Vector2i rightUv = read(obj, "right_uv", BreastArmorTexture::getAsVec2i)
				.orElseGet(() -> new Vector2i(leftUv.x + dimensions.x, leftUv.y));

		return new BreastArmorTexture(textureSize, leftUv, rightUv, dimensions);
	}

	private static Vector2i getAsVec2i(JsonElement element) {
		if(element instanceof JsonArray array && array.size() == 2) {
			if(!array.asList().stream().allMatch(elem -> elem instanceof JsonPrimitive prim && prim.isNumber())) {
				return null;
			}
			return new Vector2i(array.get(0).getAsInt(), array.get(1).getAsInt());
		}
		return null;
	}
}
