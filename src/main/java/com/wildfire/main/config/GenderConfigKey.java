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

package com.wildfire.main.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.wildfire.main.Gender;

public class GenderConfigKey extends EnumConfigKey<Gender> {
    public GenderConfigKey(String key) {
        super(key, Gender.MALE, Gender.BY_ID);
    }

    @Override
    protected Gender read(JsonElement element) {
        // TODO is this still necessary? only extraordinarily old configs should still have this as a boolean
        if(element instanceof JsonPrimitive primitive && primitive.isBoolean()) {
            return primitive.getAsBoolean() ? Gender.MALE : Gender.FEMALE;
        }
        return super.read(element);
    }
}