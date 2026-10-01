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

package com.wildfire.client.render.debug;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class DebugGroup {
    private final String name;

    //? if >=26.4-snapshot-2 {
    private final net.minecraft.client.gui.components.debug.DebugGroup vanilla;
    //?} else {
    /*private final Identifier id;
    *///?}

    // yes, many of these are going to be unused depending on the version, this is fine.
    @SuppressWarnings("unused")
    private DebugGroup(final Identifier id, final Component prettyName, final String name, final Optional<Side> preferredSide, final OptionalInt accentColor) {
        //? if <=26.3
        //this.id = id;
        this.name = name;

        //? if >=26.4-snapshot-2 {
        var builder = net.minecraft.client.gui.components.debug.DebugGroup.Builder.titled(prettyName);
        accentColor.ifPresent(builder::withAccentColor);
        preferredSide.map(Side::vanillaSide).ifPresent(builder::withPreferredColumn);
        this.vanilla = builder.build();
        //?}
    }

    public String name() {
        return name;
    }

    //? if >=26.4-snapshot-2 {
    net.minecraft.client.gui.components.debug.DebugGroup vanillaGroup() {
        return this.vanilla;
    }
    //?} else {
    /*Identifier id() {
        return this.id;
    }
    *///?}

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    public static final class Builder {
        private static final String LEGACY_PREFIX =
            ChatFormatting.GRAY + "" + ChatFormatting.UNDERLINE + "["
                + ChatFormatting.LIGHT_PURPLE + ChatFormatting.UNDERLINE + "F"
                + ChatFormatting.WHITE + ChatFormatting.UNDERLINE + "GM"
                + ChatFormatting.GRAY + ChatFormatting.UNDERLINE + "]" +
                ChatFormatting.RESET + ChatFormatting.UNDERLINE + " ";

        private static final Component PREFIX = Component.literal("[")
            .append(Component.literal("F").withColor(TextColor.LIGHT_PURPLE))
            .append(Component.literal("GM").withColor(TextColor.WHITE))
            .withColor(TextColor.GRAY)
            .append("]")
            .append(CommonComponents.SPACE);

        private Identifier id;
        private Component prettyName;
        private String name;
        private Optional<Side> preferredSide = Optional.empty();
        private OptionalInt accentColor = OptionalInt.empty();

        private Builder() {
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder named(String name) {
            this.name = LEGACY_PREFIX + name;
            this.prettyName = Component.empty().append(PREFIX).append(name);
            return this;
        }

        public Builder preferredSide(Side side) {
            this.preferredSide = Optional.of(side);
            return this;
        }

        public Builder accentColor(TextColor accentColor) {
            this.accentColor = OptionalInt.of(accentColor.getValue());
            return this;
        }

        public DebugGroup build() {
            return new DebugGroup(id, prettyName, name, preferredSide, accentColor);
        }
    }

    public enum Side {
        LEFT,
        RIGHT;

        //? if >=26.4-snapshot-2 {
        public net.minecraft.client.gui.components.debug.DebugColumn.Side vanillaSide() {
            return switch(this) {
                case LEFT -> net.minecraft.client.gui.components.debug.DebugColumn.Side.LEFT;
                case RIGHT -> net.minecraft.client.gui.components.debug.DebugColumn.Side.RIGHT;
            };
        }
        //?}
    }
}
