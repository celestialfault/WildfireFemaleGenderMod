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

import java.util.ArrayList;import java.util.function.Consumer;
import com.wildfire.api.NamedEnum;
import com.wildfire.common.config.value.ConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public record DebugFactLine(String name, Fact fact) implements DebugLine {
    public DebugFactLine(String name, Consumer<Fact> builder) {
        var fact = new Fact();
        builder.accept(fact);
        this(name, fact);
    }

    @Override
    public void addToDisplay(final DebugGroup group, final DebugScreenDisplayer displayer) {
        //? if >=26.4-snapshot-2 {
        displayer.addFactToGroup(group.vanillaGroup(), name, fact::applyToFact);
        //?} else {
        /*var string = new StringBuilder()
            .append(name)
            .append(": ")
            .append(fact.result());
        displayer.addToGroup(group.id(), string.toString());
        *///?}
    }

    /// Facade interface providing access to 26.4 debug menu facts while supporting older versions,
    /// along with supporting some value types that vanilla's fact builder doesn't.
    public static class Fact {
        //? if >=26.4-snapshot-2 {
        private final ArrayList<Consumer<net.minecraft.client.gui.components.debug.DebugFact>> lines = new ArrayList<>();
        //?} else {
        /*private final StringBuilder value = new StringBuilder();
        *///?}

        public Fact text(final Component text) {
            //? if >=26.4-snapshot-2 {
            this.lines.add(fact -> fact.text(text));
            //?} else {
            /*this.value.append(text.tryCollapseToString());
            *///?}
            return this;
        }

        public Fact text(final String text) {
            //? if >=26.4-snapshot-2 {
            this.lines.add(fact -> fact.text(text));
            //?} else {
            /*this.value.append(text);
            *///?}
            return this;
        }

        public Fact value(final String value) {
            //? if >=26.4-snapshot-2 {
            this.lines.add(fact -> fact.value(value));
            //?} else {
            /*this.value.append(value);
            *///?}
            return this;
        }

        public Fact value(final long value) {
            //? if >=26.4-snapshot-2 {
            this.lines.add(fact -> fact.value(value));
            //?} else {
            /*this.value.append(value);
            *///?}
            return this;
        }

        public Fact value(final int value) {
            //? if >=26.4-snapshot-2 {
            this.lines.add(fact -> fact.value(value));
            //?} else {
            /*this.value.append(value);
            *///?}
            return this;
        }

        public Fact formattedValue(final String format, final Object... args) {
            //? if >=26.4-snapshot-2 {
            this.lines.add(fact -> fact.formattedValue(format, args));
            //?} else {
            /*this.value.append(String.format(java.util.Locale.ROOT, format, args));
            *///?}
            return this;
        }

        //<editor-fold desc="Non-standard overloads">
        public Fact value(ChatFormatting color, String value) {
            //? if >=26.4-snapshot-2 {
            return text(Component.literal(value).withStyle(color));
            //?} else {
            /*return value(color + value);
            *///?}
        }

        public Fact value(boolean value) {
            //? if >=26.4-snapshot-2 {
            if(value) {
                return text(Component.literal("true").withColor(TextColor.GREEN));
            }
            return text(Component.literal("false").withColor(TextColor.RED));
            //?} else {
            /*return value(Boolean.toString(value));
            *///?}
        }

        public Fact value(float value) {
            return value(Float.toString(value));
        }

        public Fact value(ConfigValue<?> value) {
            Object val = value.get();
            if(val instanceof Boolean bool) {
                return value(bool);
            }
            return value(val.toString());
        }

        public Fact value(Enum<?> value) {
            //? if >=26.4-snapshot-2 {
            if(value instanceof NamedEnum named) {
                return text(named.getTranslatedName());
            }
            //?}
            return value(value.name());
        }
        //</editor-fold>

        //? if <=26.3 {
        /*private StringBuilder result() {
            return value;
        }
        *///?} else {
        private void applyToFact(net.minecraft.client.gui.components.debug.DebugFact fact) {
            for(var line : lines) {
                line.accept(fact);
            }
        }
        //?}
    }
}
