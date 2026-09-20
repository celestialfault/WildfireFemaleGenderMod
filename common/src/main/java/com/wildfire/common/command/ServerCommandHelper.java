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

package com.wildfire.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.Permissions;

public interface ServerCommandHelper extends CommandHelper<CommandSourceStack> {
    Permission SERVER_COMMAND = Permissions.COMMANDS_GAMEMASTER;
    Permission DEBUG_COMMANDS = Permissions.COMMANDS_GAMEMASTER;

    MinecraftServer getServer(CommandSourceStack source);
    ServerPlayer getPlayer(CommandSourceStack source) throws CommandSyntaxException;

    default boolean hasCommandPermission(CommandSourceStack source) {
        return source.permissions().hasPermission(SERVER_COMMAND);
    }

    default boolean hasDebugCommandPermission(CommandSourceStack source) {
        return source.permissions().hasPermission(DEBUG_COMMANDS);
    }
}
