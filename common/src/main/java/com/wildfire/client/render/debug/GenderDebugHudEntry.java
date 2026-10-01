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

import com.wildfire.api.IGenderArmor;
import com.wildfire.api.client.WildfireClientAPI;
import com.wildfire.client.physics.BreastPhysics;
import com.wildfire.client.resources.GenderArmorResourceManager;
import com.wildfire.common.WildfireGender;
import com.wildfire.common.entities.EntityConfigHolder;
import java.util.function.Function;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class GenderDebugHudEntry implements DebugScreenEntry {
    public static final Identifier SELF = WildfireGender.id("self_gender_info");
    public static final Identifier OTHER = WildfireGender.id("target_gender_info");

    private final DebugGroup mainGroup, chestplateGroup;

    private final boolean clientPlayer;

    public GenderDebugHudEntry(boolean clientPlayer) {
        this.clientPlayer = clientPlayer;
        //~ color_as_rgb !named_text_color
        this.mainGroup = DebugGroup.builder()
            .id(clientPlayer ? SELF : OTHER)
            .named("Gender Data")
            .accentColor(TextColor.LIGHT_PURPLE)
            .preferredSide(clientPlayer ? DebugGroup.Side.RIGHT : DebugGroup.Side.LEFT)
            .build();

        this.chestplateGroup = DebugGroup.builder()
            .id(clientPlayer ? SELF : OTHER)
            .named("Equipped Chestplate")
            .accentColor(TextColor.AQUA)
            .preferredSide(clientPlayer ? DebugGroup.Side.RIGHT : DebugGroup.Side.LEFT)
            .build();
        //~ !color_as_rgb named_text_color
    }

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level world, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        var client = Minecraft.getInstance();
        var target = clientPlayer ? client.player : client.crosshairPickEntity;
        if(!(target instanceof LivingEntity living)) {
            return;
        }

        var config = WildfireClientAPI.getConfig(living);
        if(config == null) {
            return;
        }

        var lines = new DebugLines(/*? <=26.3 >> ')'*//*mainGroup*/);
        lines.addAll(config.getDebugInfo());

        //? if >=26.4-snapshot-2 {
        lines.submit(mainGroup, displayer);
        addEquippedChestplate(displayer, config, living);
        //?} else {
        /*addEquippedChestplate(lines, config, living);
        lines.submit(mainGroup, displayer);
        *///?}
    }

    private void addEquippedChestplate(
        //? if >=26.4-snapshot-2 {
        DebugScreenDisplayer displayer,
        //?} else {
        /*DebugLines parent,
        *///?}
        EntityConfigHolder<?> config,
        LivingEntity entity
    ) {
        var equippedChestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        var equippable = equippedChestplate.get(DataComponents.EQUIPPABLE);
        // null is perfectly valid to return here
        //noinspection DataFlowIssue
        var asset = Optionull.map(equippable, it -> it.assetId().orElse(null));
        if(asset == null) {
            return;
        }

        var lines = new DebugLines(/*? <=26.3 >> ')'*//*chestplateGroup*/);

        var id = asset.identifier();
        var armorConfig = Optionull.mapOrDefault(GenderArmorResourceManager.get(id), Function.identity(), IGenderArmor.DEFAULT);
        lines.fact("Material", fact -> fact.value(id.toString()));
        if(!armorConfig.coversBreasts()) {
            lines.fact("Covers breasts", fact -> fact.value(false));
        } else if(armorConfig.alwaysHidesBreasts()) {
            lines.fact("Hides breasts", fact -> fact.value(true));
        } else {
            lines.fact("Physics resistance", fact -> fact.value(Float.toString(armorConfig.physicsResistance())));
            lines.fact("Tightness", fact -> fact.value(Float.toString(armorConfig.tightness())));
            lines.fact("Armor stands copy", fact -> fact.value(armorConfig.armorStandsCopySettings()));
            if (armorConfig.tightness() > 0) {
                float renderedSize = config.breasts().bustSize().get() * (1 - BreastPhysics.TIGHTNESS_REDUCTION_FACTOR * armorConfig.tightness());
                lines.fact("Rendered breast size", fact -> fact.value(Float.toString(renderedSize)));
            }
        }

        //? if >=26.4-snapshot-2 {
        lines.submit(chestplateGroup, displayer);
        //?} else {
        /*parent.literal("");
        parent.addHeader(lines);
        parent.addAll(lines);
        *///?}
    }
}
