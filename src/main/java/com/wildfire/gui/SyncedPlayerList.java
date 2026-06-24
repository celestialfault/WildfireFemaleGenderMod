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

package com.wildfire.gui;

import com.wildfire.gui.screen.BaseWildfireScreen;
import com.wildfire.main.WildfireGenderClient;
import com.wildfire.main.config.enums.Gender;
import com.wildfire.main.contributors.Contributors;
import com.wildfire.main.entitydata.PlayerConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class SyncedPlayerList {
    private SyncedPlayerList() {
        throw new UnsupportedOperationException();
    }

    private static long TICK = -1;
    private static List<SyncedPlayer> SYNCED_PLAYERS = Collections.emptyList();

    public static void drawSyncedPlayers(GuiGraphicsExtractor context, Font font) {
        var syncedPlayers = getSyncedPlayers();
        if(syncedPlayers.isEmpty()) {
            return;
        }

        var header = Component.translatable("wildfire_gender.wardrobe.players_using_mod").withStyle(ChatFormatting.AQUA);
        context.text(font, header, 5, 5, 0xFFFFFFFF, true);

        int line = 0;
        for(var entry : syncedPlayers) {
            context.text(font, entry.line(), 10, 18 + (10 * line++), 0xFFFFFFFF, false);
        }
    }

    private static List<SyncedPlayer> getSyncedPlayers() {
        var client = Minecraft.getInstance();
        long currentTick = client.clientTickCount;
        if(currentTick != TICK) {
            TICK = currentTick;
            SYNCED_PLAYERS = computeSyncedList();
        }
        return SYNCED_PLAYERS;
    }

    private static List<SyncedPlayer> computeSyncedList() {
        var client = Minecraft.getInstance();
        var clientPlayer = client.player;
        if(clientPlayer == null) {
            SYNCED_PLAYERS = Collections.emptyList();
            return Collections.emptyList();
        }

        int max = (client.getWindow().getGuiScaledHeight() - 32) / 10;
        //~ if >=26.2 'client.screen' -> 'client.gui.screen()'
        if(!(client.gui.screen() instanceof BaseWildfireScreen)) {
            max = Math.max(3, max / 5);
        }
        List<SyncedPlayer> list = new ArrayList<>();

        for(var entry : clientPlayer.connection.getListedOnlinePlayers()) {
            if(Objects.equals(entry.getProfile().id(), clientPlayer.getUUID())) {
                continue;
            }

            var config = WildfireGenderClient.getPlayerById(entry.getProfile().id());
            if(config == null || config.syncStatus == PlayerConfig.SyncStatus.UNKNOWN) {
                continue;
            }

            var color = Contributors.getColor(entry.getProfile().id());
            list.add(new SyncedPlayer.Player(entry.getProfile().name(), color == null ? 0xFFFFFF : color, config.getGender()));
        }

        if(list.size() > max) {
            int overflow = list.size() - max;
            list = list.subList(0, max - 1);
            list.add(new SyncedPlayer.Overflow(overflow));
        }

        return list;
    }

    private interface SyncedPlayer {
        Component line();

        record Player(String name, int color, Gender gender) implements SyncedPlayer {
            @Override
            public Component line() {
                return Component.empty()
                    .append(Component.literal(name).withColor(color))
                    .append(" - ")
                    .append(gender.getDisplayName());
            }
        }

        record Overflow(int remaining) implements SyncedPlayer {
            @Override
            public Component line() {
                return Component.translatable("wildfire_gender.wardrobe.players_using_mod.overflow", remaining)
                    .withStyle(ChatFormatting.GRAY);
            }
        }
    }
}
