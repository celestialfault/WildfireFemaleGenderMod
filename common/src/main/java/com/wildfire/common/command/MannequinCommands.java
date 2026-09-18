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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JavaOps;
import com.wildfire.api.server.WildfireServerAPI;
import com.wildfire.common.entities.avatars.AbstractAvatarConfigHolder;
import com.wildfire.common.entities.avatars.AvatarConfig;
import com.wildfire.common.entities.avatars.MannequinConfigHolder;
import com.wildfire.common.networking.WildfireNetworking;
import com.wildfire.common.networking.WildfireSync;
import com.wildfire.common.networking.packets.mannequins.ClientboundEditMannequinPacket;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Mannequin;

public final class MannequinCommands<S extends SharedSuggestionProvider> extends AbstractWildfireCommand<ServerCommandHelper<S>, S> {
    MannequinCommands(final ServerCommandHelper<S> helper) {
        super(helper);
    }

    public LiteralArgumentBuilder<S> createNode() {
        return helper.literal("mannequin")
            .then(helper.argument("entity", EntityArgument.entity())
                .then(helper.literal("gui")
                    .executes(this::editMannequin))
                .then(helper.literal("copy")
                    .then(helper.argument("from", EntityArgument.entity())
                        .executes(this::copyOntoMannequin)))
            // TODO add commands for each (useful) option?
            );
    }

    private int editMannequin(CommandContext<S> ctx) throws CommandSyntaxException {
        ServerPlayer player = helper.getPlayer(ctx.getSource());
        Entity entity = helper.resolveSingleEntityArgument(ctx, "entity");
        if(!(entity instanceof Mannequin)) {
            // TODO feedback
            return 0;
        }

        if(WildfireNetworking.INSTANCE.canSendToPlayer(player, ClientboundEditMannequinPacket.TYPE)) {
            WildfireNetworking.INSTANCE.sendToClient(player, new ClientboundEditMannequinPacket(entity.getUUID()));
        } else {
            // TODO feedback
        }

        return Command.SINGLE_SUCCESS;
    }

    private int copyOntoMannequin(CommandContext<S> ctx) throws CommandSyntaxException {
        Entity entity = helper.resolveSingleEntityArgument(ctx, "entity");
        if(!(entity instanceof Mannequin mannequin)) {
            // TODO feedback
            return 0;
        }

        Entity from = helper.resolveSingleEntityArgument(ctx, "from");
        if(!(from instanceof LivingEntity fromLiving)) {
            // TODO feedback
            return 0;
        }
        var fromConfig = WildfireServerAPI.getConfig(fromLiving);
        if(!(fromConfig instanceof AbstractAvatarConfigHolder fromAvatarConfig)) {
            // TODO feedback
            return 0;
        }

        MannequinConfigHolder config = WildfireServerAPI.mannequins().getOrCreate(mannequin);
        config.setConfig(
            AvatarConfig.CODEC.decode(
                JavaOps.INSTANCE,
                AvatarConfig.CODEC.encodeStart(
                    JavaOps.INSTANCE,
                    fromAvatarConfig.config()
                ).result().orElseGet(AvatarConfig::createDefault)
            ).getOrThrow().getFirst()
        );
        WildfireSync.sendToAllClients(mannequin, config);

        return Command.SINGLE_SUCCESS;
    }
}
