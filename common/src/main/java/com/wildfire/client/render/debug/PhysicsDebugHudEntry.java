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

import com.wildfire.api.client.WildfireClientAPI;
import com.wildfire.client.physics.BothBreastsPhysics;
import com.wildfire.client.physics.BreastPhysics;
import com.wildfire.client.render.BreastSide;
import com.wildfire.common.WildfireGender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class PhysicsDebugHudEntry implements DebugScreenEntry {
    public static final Identifier ID = WildfireGender.id("physics");

    private static final DebugGroup GROUP = DebugGroup.builder()
        .named("Breast Physics Values")
        //~ color_as_rgb !named_text_color
        .accentColor(TextColor.DARK_PURPLE)
        //~ !color_as_rgb named_text_color
        .preferredSide(DebugGroup.Side.LEFT)
        .build();

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level world, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        var player = Minecraft.getInstance().player;
        if(player == null) return;
        var config = WildfireClientAPI.players().get(player);
        if(config == null) return;

        BothBreastsPhysics breastPhysics = config.breastPhysics();
        var lines = new DebugLines(/*? <=26.3 >> ')'*//*GROUP*/);

        var swingState = BreastPhysics.getSwingState(player);
        if(!swingState.isSwinging()) {
            lines.fact("Arm swinging", fact -> fact.value(false));
        } else {
            lines.fact("Arm swinging", fact -> fact.value(true));
            lines.fact("Duration", fact -> fact.value(swingState.tick()).text("/").value(swingState.duration()));
            lines.fact("Swing effect amplifiers", fact -> fact.value(swingState.amplifier()).text(" (").value(swingState.xAmplifier()).text(")"));
        }

        if(config.breasts().physics().uniboob().get()) {
            add(lines, null, breastPhysics.left());
        } else {
            add(lines, BreastSide.LEFT, breastPhysics.left());
            add(lines, BreastSide.RIGHT, breastPhysics.right());
        }

        lines.submit(GROUP, displayer);
    }

    private void add(DebugLines lines, @Nullable BreastSide side, BreastPhysics physics) {
        String sideName = side == null ? "Uniboob" : side == BreastSide.LEFT ? "Left" : "Right";
        lines.fact(sideName + " breast size", fact -> fact.value(physics.getBreastSize()));
        lines.fact(sideName + " position", fact -> fact.text("(").value(physics.getPositionX()).text(", ").value(physics.getPositionY()).text(")"));
        lines.fact(sideName + " rotation", fact -> fact.value(physics.getBounceRotation()));
    }
}
