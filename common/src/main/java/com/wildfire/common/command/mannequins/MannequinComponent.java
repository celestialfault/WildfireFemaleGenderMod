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

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.wildfire.api.Gender;
import com.wildfire.common.WildfireLang;
import com.wildfire.common.command.ServerCommandHelper;
import com.wildfire.common.config.validator.ConfigRange;
import com.wildfire.common.config.value.ConfigKey;
import com.wildfire.common.config.value.ConfigValue;
import com.wildfire.common.entities.Breasts;
import com.wildfire.common.entities.EntityConfigHolder;
import com.wildfire.common.entities.Sounds;
import com.wildfire.common.entities.avatars.AbstractAvatarConfigHolder;
import com.wildfire.common.entities.avatars.MannequinConfigHolder;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import org.jspecify.annotations.Nullable;

/*package-private*/ final class MannequinComponent<T> {
    private static final DynamicCommandExceptionType INVALID_GENDER_INPUT = WildfireLang.COMMAND_SERVER_INVALID_GENDER.dynamicCommandException();

    // note that this must be presented to clients as a string argument, otherwise they will disconnect upon being
    // given an unrecognized argument type as part of these commands
    public static final MannequinComponent<Gender> GENDER = of(StringArgumentType.word(), EntityConfigHolder::gender, (ctx, name) -> {
        String input = StringArgumentType.getString(ctx, name);
        try {
            return Gender.valueOf(input.toUpperCase(Locale.ROOT));
        } catch(IllegalArgumentException _) {
            throw INVALID_GENDER_INPUT.create(input);
        }
    }).suggests(new GenderSuggestionProvider<>());

    public static final MannequinComponent<Float> BREAST_SIZE = of(Breasts.BUST_SIZE, config -> config.breasts().bustSize());
    public static final MannequinComponent<Float> BREAST_OFFSET_X = of(Breasts.BREASTS_OFFSET_X, config -> config.breasts().xOffset());
    public static final MannequinComponent<Float> BREAST_OFFSET_Y = of(Breasts.BREASTS_OFFSET_Y, config -> config.breasts().yOffset());
    public static final MannequinComponent<Float> BREAST_OFFSET_Z = of(Breasts.BREASTS_OFFSET_Z, config -> config.breasts().zOffset());
    public static final MannequinComponent<Float> BREAST_CLEAVAGE = of(Breasts.BREASTS_CLEAVAGE, config -> config.breasts().cleavage());
    public static final MannequinComponent<Boolean> SHOW_IN_ARMOR = of(AbstractAvatarConfigHolder::showBreastsInArmor);

    public static final MannequinComponent<Boolean> PHYSICS = of(config -> config.breasts().physics().enabled());
    public static final MannequinComponent<Float> PHYSICS_BOUNCE = of(Breasts.Physics.BOUNCE_MULTIPLIER, config -> config.breasts().physics().bounceMultiplier());
    public static final MannequinComponent<Float> PHYSICS_FLOPPY = of(Breasts.Physics.FLOPPINESS, config -> config.breasts().physics().floppiness());
    public static final MannequinComponent<Boolean> PHYSICS_UNIBOOB = of(config -> config.breasts().physics().uniboob());

    public static final MannequinComponent<Boolean> HURT_SOUNDS = of(config -> config.sounds().hurt());
    public static final MannequinComponent<Float> VOICE_PITCH = of(Sounds.VOICE_PITCH, config -> config.sounds().voicePitch());

    private final ArgumentType<?> argument;
    private final Parser<T> parser;
    private final ConfigValueGetter<T> value;
    private @Nullable SuggestionProvider<CommandSourceStack> suggestionProvider = null;

    private MannequinComponent(ArgumentType<?> argument, ConfigValueGetter<T> value, Parser<T> parser) {
        this.argument = argument;
        this.value = value;
        this.parser = parser;
    }

    private static <T> MannequinComponent<T> of(ArgumentType<T> argument, ConfigValueGetter<T> value, Class<T> type) {
        return new MannequinComponent<>(argument, value, (ctx, arg) -> ctx.getArgument(arg, type));
    }

    private static MannequinComponent<Float> of(ConfigKey<Float> key, ConfigValueGetter<Float> value) {
        var validator = key.validator();
        final ArgumentType<Float> argument;
        if(validator instanceof ConfigRange<Float>(Float minInclusive, Float maxInclusive)) {
            argument = FloatArgumentType.floatArg(minInclusive, maxInclusive);
        } else {
            argument = FloatArgumentType.floatArg();
        }
        return of(argument, value, Float.class);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> MannequinComponent<T> of(ArgumentType<?> argument, ConfigValueGetter<T> value, Parser<T> parser) {
        return new MannequinComponent<>(argument, value, parser);
    }

    private static MannequinComponent<Boolean> of(ConfigValueGetter<Boolean> value) {
        return of(BoolArgumentType.bool(), value, Boolean.class);
    }

    public RequiredArgumentBuilder<CommandSourceStack, ?> argument(ServerCommandHelper helper, String name) {
        return helper.argument(name, argument)
            // this is safe to pass a possibly null value to as it simply sets a field which is null by default
            .suggests(suggestionProvider);
    }

    public boolean update(MannequinConfigHolder config, CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        T input = parser.parse(ctx, name);
        ConfigValue<T> value = this.value.get(config);
        if(Objects.equals(value.get(), input)) {
            return false;
        }
        return value.update(input);
    }

    MannequinComponent<T> suggests(SuggestionProvider<CommandSourceStack> provider) {
        this.suggestionProvider = provider;
        return this;
    }

    @FunctionalInterface
    private interface Parser<T> {
        T parse(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface ConfigValueGetter<T> {
        ConfigValue<T> get(MannequinConfigHolder config);
    }
}
