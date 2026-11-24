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

package com.wildfire.common.command.mannequins;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.wildfire.api.Gender;
import net.minecraft.commands.CommandSourceStack;
import java.util.Arrays;
import java.util.stream.Stream;

public final class GenderSuggestionProvider {
    private GenderSuggestionProvider() {
    }

    //? if <=26.3 {
    /*private static final SuggestionProvider<CommandSourceStack> INSTANCE = (_, builder) ->
        net.minecraft.commands.SharedSuggestionProvider.suggest(names(), builder);
    *///?}

    public static SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
        //? if >=26.4-* {
        return net.minecraft.commands.suggestions.SuggestLiteralString.suggestLiteral(names().toArray(String[]::new));
        //?} else {
        /*return INSTANCE;
        *///?}
    }

    private static Stream<String> names() {
        return Arrays.stream(Gender.values()).map(Gender::name);
    }
}
