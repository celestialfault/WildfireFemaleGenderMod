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
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.ChatFormatting;

public class GenderDebugHudEntry implements DebugScreenEntry {
    public static final Identifier SELF = WildfireGender.id("self_gender_info");
    public static final Identifier OTHER = WildfireGender.id("target_gender_info");

    //? if <=26.3 {
    /*private static final String PREFIX =
            ChatFormatting.GRAY + "" + ChatFormatting.UNDERLINE + "["
                    + ChatFormatting.LIGHT_PURPLE + ChatFormatting.UNDERLINE + "F"
                    + ChatFormatting.WHITE + ChatFormatting.UNDERLINE + "GM"
                    + ChatFormatting.GRAY + ChatFormatting.UNDERLINE + "]" +
                    ChatFormatting.RESET + ChatFormatting.UNDERLINE;
    private final Identifier id;
    *///?} else {
    static final net.minecraft.network.chat.Component PREFIX = net.minecraft.network.chat.Component.empty()
        .append(net.minecraft.network.chat.Component.literal("F").withColor(net.minecraft.network.chat.TextColor.LIGHT_PURPLE))
        .append("GM")
        .withColor(net.minecraft.network.chat.TextColor.WHITE);

    private final net.minecraft.client.gui.components.debug.DebugGroup group, chestplateGroup;
    //?}

    private final boolean clientPlayer;

    public GenderDebugHudEntry(boolean clientPlayer) {
        this.clientPlayer = clientPlayer;
        //? if >=26.4-snapshot-2 {
        var mainTitle = net.minecraft.network.chat.Component.empty().append(PREFIX).append(" - Gender Info");
        var chestplateTitle = net.minecraft.network.chat.Component.empty().append(PREFIX).append(" - Equipped Chestplate");
        var preferredSide = clientPlayer ? net.minecraft.client.gui.components.debug.DebugColumn.Side.RIGHT :
            net.minecraft.client.gui.components.debug.DebugColumn.Side.LEFT;

        group = net.minecraft.client.gui.components.debug.DebugGroup.Builder.titled(mainTitle)
            .withPreferredColumn(preferredSide)
            .withAccentColor(TextColor.LIGHT_PURPLE.getValue())
            .build();

        chestplateGroup = net.minecraft.client.gui.components.debug.DebugGroup.Builder.titled(chestplateTitle)
            .withAccentColor(TextColor.AQUA.getValue())
            .withPreferredColumn(preferredSide)
            .build();
        //?} else {
        /*this.id = clientPlayer ? SELF : OTHER;
        *///?}
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

        List<String> lines = new ArrayList<>();
        //? if <=26.3
        //lines.add(PREFIX + " Gender Data");
        lines.add("UUID: " + target.getUUID());
        lines.addAll(config.getDebugInfo());

        //~ if >=26.4-snapshot-2 id -> group
        displayer.addToGroup(group, lines);
        addEquippedChestplate(displayer, config, living);
    }

    private void addEquippedChestplate(DebugScreenDisplayer displayer, EntityConfigHolder<?> config, LivingEntity entity) {
        var equippedChestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        var equippable = equippedChestplate.get(DataComponents.EQUIPPABLE);
        // null is perfectly valid to return here
        //noinspection DataFlowIssue
        var asset = Optionull.map(equippable, it -> it.assetId().orElse(null));
        if(asset == null) return;

        var lines = new ArrayList<String>();
        lines.add("");
        //? if <=26.3
        //lines.add(PREFIX + " Equipped Chestplate");

        var id = asset.identifier();
        var armorConfig = Optionull.mapOrDefault(GenderArmorResourceManager.get(id), Function.identity(), IGenderArmor.DEFAULT);
        lines.add("Material: " + id);
        if(!armorConfig.coversBreasts()) {
            lines.add("Covers breasts: false");
            return;
        } else if(armorConfig.alwaysHidesBreasts()) {
            lines.add("Covers breasts: true");
            return;
        }
        lines.add("Physics resistance: " + armorConfig.physicsResistance());
        lines.add("Tightness: " + armorConfig.tightness());
        lines.add("Armor stands copy: " + armorConfig.armorStandsCopySettings());
        if(armorConfig.tightness() > 0) {
            float renderedSize = config.breasts().bustSize().get() * (1 - BreastPhysics.TIGHTNESS_REDUCTION_FACTOR * armorConfig.tightness());
            lines.add("Rendered breast size: " + renderedSize);
        }

        //~ if >=26.4-snapshot-2 id -> chestplateGroup
        displayer.addToGroup(chestplateGroup, lines);
    }
}
