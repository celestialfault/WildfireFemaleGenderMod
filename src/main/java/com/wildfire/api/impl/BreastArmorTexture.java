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
import com.wildfire.api.Vec2i;

import java.util.Arrays;

import static com.wildfire.main.WildfireHelper.read;

/**
 * @see IBreastArmorTexture
 */
public record BreastArmorTexture(@NotNull Vec2i textureSize, @NotNull Vec2i leftUv, @NotNull Vec2i rightUv, @NotNull Vec2i dimensions) implements IBreastArmorTexture {

	private static final Vec2i DEFAULT_TEXTURE_SIZE = new Vec2i(64, 32);
	private static final Vec2i DEFAULT_DIMENSIONS = new Vec2i(4, 5);
	private static final Vec2i DEFAULT_LEFT_UV = new Vec2i(16, 17);
	private static final String[] EXPECTED_KEYS = new String[] { "texture_size", "dimensions", "uv", "right_uv" };

	// TODO document the json syntax
	public static IBreastArmorTexture fromJson(JsonObject obj) {
		// avoid creating redundant record instances if we don't need to
		if(Arrays.stream(EXPECTED_KEYS).noneMatch(obj::has)) {
			return DefaultBreastArmorTexture.DEFAULT;
		}

		final Vec2i textureSize = read(obj, "texture_size", BreastArmorTexture::getAsVec2i).orElse(DEFAULT_TEXTURE_SIZE);
		final Vec2i dimensions = read(obj, "dimensions", BreastArmorTexture::getAsVec2i).orElse(DEFAULT_DIMENSIONS);
		final Vec2i leftUv = read(obj, "uv", BreastArmorTexture::getAsVec2i).orElse(DEFAULT_LEFT_UV);
		final Vec2i rightUv = read(obj, "right_uv", BreastArmorTexture::getAsVec2i)
				.orElseGet(() -> leftUv.withX(leftUv.x() + dimensions.x()));

		return new BreastArmorTexture(textureSize, leftUv, rightUv, dimensions);
	}

	private static Vec2i getAsVec2i(JsonElement element) {
		if(element instanceof JsonArray array && array.size() == 2) {
			if(!array.asList().stream().allMatch(elem -> elem instanceof JsonPrimitive prim && prim.isNumber())) {
				return null;
			}
			return new Vec2i(array.get(0).getAsInt(), array.get(1).getAsInt());
		}
		return null;
	}
}
