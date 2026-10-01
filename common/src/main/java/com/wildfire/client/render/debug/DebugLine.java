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

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import java.util.function.Consumer;

public sealed interface DebugLine permits DebugFactLine,  DebugLine.Literal {
    static DebugLine fact(String name, Consumer<DebugFactLine.Fact> builder) {
        return new DebugFactLine(name, builder);
    }

    static DebugLine literal(String literal) {
        return new Literal(literal);
    }

    void addToDisplay(DebugGroup group, DebugScreenDisplayer displayer);

    public record Literal(String value) implements DebugLine {
        @Override
        public void addToDisplay(final DebugGroup group, final DebugScreenDisplayer displayer) {
            //? if >=26.4-snapshot-2 {
            displayer.addToGroup(group.vanillaGroup(), value);
            //?} else {
            /*displayer.addToGroup(group.id(), value);
            *///?}
        }
    }
}
