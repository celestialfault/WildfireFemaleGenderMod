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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public final class DebugLines {
    //? if <=26.3 {
    /*@org.jspecify.annotations.Nullable
    private final String header;
    *///?}

    private final List<DebugLine> lines = new ArrayList<>();

    //? if <=26.3 {
    /*public DebugLines(DebugGroup group) {
        this.header = group.name();
    }
    *///?}

    public DebugLines() {
        //? if <=26.3
        //this.header = null;
    }

    public DebugLines addFirst(DebugLine line) {
        lines.addFirst(line);
        return this;
    }

    public DebugLines add(DebugLine line) {
        lines.add(line);
        return this;
    }

    public DebugLines add(int index, DebugLine line) {
        lines.add(index, line);
        return this;
    }

    public DebugLines addAll(DebugLines lines) {
        this.lines.addAll(lines.lines);
        return this;
    }

    //? if <=26.3 {
    /*public DebugLines addHeader(DebugLines lines) {
        return literal(lines.header);
    }
    *///?}

    // a note on 26.4 behavior: facts will always be above other lines (what we call literal lines here),
    // meaning it isn't possible to further separate groups of facts with literal lines
    public DebugLines fact(String name, Consumer<DebugFactLine.Fact> builder) {
        return add(DebugLine.fact(name, builder));
    }

    public DebugLines literal(String literal) {
        return add(DebugLine.literal(literal));
    }

    void submit(DebugGroup group, DebugScreenDisplayer displayer) {
        //? if <=26.3 {
        /*if(header != null) {
            displayer.addToGroup(group.id(), header);
        }
        *///?}

        for(var line : this.lines) {
            line.addToDisplay(group, displayer);
        }
    }
}
