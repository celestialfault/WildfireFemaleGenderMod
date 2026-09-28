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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wildfire.api.Gender;
import com.wildfire.common.WildfireLang;
import com.wildfire.common.config.validator.ConfigRange;
import com.wildfire.common.config.value.ConfigKey;
import com.wildfire.common.entities.Breasts;
import com.wildfire.common.entities.EntityConfigHolder;
import com.wildfire.common.entities.Sounds;
import com.wildfire.common.entities.avatars.AbstractAvatarConfigHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/*package-private*/ final class MannequinComponents {
    private static final DynamicCommandExceptionType INVALID_GENDER_INPUT = WildfireLang.COMMAND_SERVER_INVALID_GENDER.dynamicCommandException();
    private static final List<MannequinComponent<?>> components = new ArrayList<>();

    // note that this must be presented to clients as a string argument, otherwise they will disconnect upon being
    // given an unrecognized argument type as part of these commands
    public static final MannequinComponent<Gender> GENDER = register(MannequinComponent.<Gender>builder()
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_GENDER)
        .argument(StringArgumentType.word())
        .parser((ctx, name) -> {
            String input = StringArgumentType.getString(ctx, name);
            try {
                return Gender.valueOf(input.toUpperCase(Locale.ROOT));
            } catch(IllegalArgumentException _) {
                throw INVALID_GENDER_INPUT.create(input);
            }
        })
        .getter(EntityConfigHolder::gender)
        .suggests(new GenderSuggestionProvider<>())
        .build());

    public static final MannequinComponent<Float> BREAST_SIZE = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_BREAST_SIZE)
        .argument(boundedFloat(Breasts.BUST_SIZE))
        .getter(config -> config.breasts().bustSize())
        .build());

    public static final MannequinComponent<Float> BREAST_OFFSET_X = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_X_OFFSET)
        .argument(boundedFloat(Breasts.BREASTS_OFFSET_X))
        .getter(config -> config.breasts().xOffset())
        .build());
    public static final MannequinComponent<Float> BREAST_OFFSET_Y = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_Y_OFFSET)
        .argument(boundedFloat(Breasts.BREASTS_OFFSET_Y))
        .getter(config -> config.breasts().yOffset())
        .build());
    public static final MannequinComponent<Float> BREAST_OFFSET_Z = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_Z_OFFSET)
        .argument(boundedFloat(Breasts.BREASTS_OFFSET_Z))
        .getter(config -> config.breasts().zOffset())
        .build());

    public static final MannequinComponent<Float> BREAST_CLEAVAGE = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_CLEAVAGE)
        .argument(boundedFloat(Breasts.BREASTS_CLEAVAGE))
        .getter(config -> config.breasts().cleavage())
        .build());

    public static final MannequinComponent<Boolean> SHOW_IN_ARMOR = register(MannequinComponent.builder(Boolean.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_SHOW_IN_ARMOR)
        .argument(BoolArgumentType.bool())
        .getter(AbstractAvatarConfigHolder::showBreastsInArmor)
        .build());

    public static final MannequinComponent<Boolean> PHYSICS = register(MannequinComponent.builder(Boolean.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_PHYSICS)
        .argument(BoolArgumentType.bool())
        .getter(config -> config.breasts().physics().enabled())
        .build());
    public static final MannequinComponent<Float> PHYSICS_BOUNCE = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_PHYSICS_BOUNCE)
        .argument(boundedFloat(Breasts.Physics.BOUNCE_MULTIPLIER))
        .getter(config -> config.breasts().physics().bounceMultiplier())
        .build());
    public static final MannequinComponent<Float> PHYSICS_FLOPPY = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_PHYSICS_FLOPPY)
        .argument(boundedFloat(Breasts.Physics.FLOPPINESS))
        .getter(config -> config.breasts().physics().floppiness())
        .build());
    public static final MannequinComponent<Boolean> PHYSICS_UNIBOOB = register(MannequinComponent.builder(Boolean.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_PHYSICS_UNIBOOB)
        .argument(BoolArgumentType.bool())
        .getter(config -> config.breasts().physics().uniboob())
        .build());

    public static final MannequinComponent<Boolean> HURT_SOUNDS = register(MannequinComponent.builder(Boolean.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_HURT_SOUNDS)
        .argument(BoolArgumentType.bool())
        .getter(config -> config.sounds().hurt())
        .build());
    public static final MannequinComponent<Float> VOICE_PITCH = register(MannequinComponent.builder(Float.class)
        .name(WildfireLang.COMMAND_MANNEQUIN_DATA_HURT_PITCH)
        .argument(boundedFloat(Sounds.VOICE_PITCH))
        .getter(config -> config.sounds().voicePitch())
        .build());

    private static ArgumentType<Float> boundedFloat(ConfigKey<Float> key) {
        if(key.validator() instanceof ConfigRange<Float>(Float minInclusive, Float maxInclusive)) {
            return FloatArgumentType.floatArg(minInclusive, maxInclusive);
        }
        return FloatArgumentType.floatArg();
    }

    private static <T> MannequinComponent<T> register(MannequinComponent<T> component) {
        components.add(component);
        return component;
    }

    public static List<MannequinComponent<?>> components() {
        return Collections.unmodifiableList(components);
    }

    private MannequinComponents() {
    }
}
