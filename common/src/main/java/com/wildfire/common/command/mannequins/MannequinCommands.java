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

package com.wildfire.common.command.mannequins;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.wildfire.api.server.WildfireServerAPI;
import com.wildfire.common.WildfireLang;
import com.wildfire.common.command.AbstractWildfireCommand;
import com.wildfire.common.command.ServerCommandHelper;
import com.wildfire.common.entities.avatars.AbstractAvatarConfigHolder;
import com.wildfire.common.entities.avatars.MannequinConfigHolder;
import com.wildfire.common.networking.WildfireNetworking;
import com.wildfire.common.networking.packets.mannequins.ClientboundEditMannequinPacket;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Mannequin;

public final class MannequinCommands extends AbstractWildfireCommand<ServerCommandHelper, CommandSourceStack> {
    private static final SimpleCommandExceptionType NOT_A_MANNEQUIN = WildfireLang.COMMAND_ENTITY_MUST_BE_MANNEQUIN.simpleCommandException();
    private static final SimpleCommandExceptionType TARGET_NOT_AVATAR = WildfireLang.COMMAND_ENTITY_MUST_BE_AVATAR_LIKE.simpleCommandException();

    public MannequinCommands(final ServerCommandHelper helper) {
        super(helper);
    }

    public LiteralArgumentBuilder<CommandSourceStack> createNode() {
        return helper.literal("mannequin")
            .then(helper.argument("mannequin", EntityArgument.entity())
                .then(helper.literal("gui")
                    .executes(this::editMannequin))
                .then(helper.literal("copy")
                    .then(helper.argument("from", EntityArgument.entity())
                        // TODO does it make sense to allow for copying a single component from a player/other mannequin?
                        // TODO allow for copying UVs?
                        .executes(this::copyAllOntoMannequin)))
                .then(setter("gender", MannequinComponents.GENDER))
                .then(helper.literal("breast")
                    .then(setter("size", MannequinComponents.BREAST_SIZE))
                    .then(setter("rotation", MannequinComponents.BREAST_CLEAVAGE))
                    .then(helper.literal("physics")
                        .then(setter("enabled", MannequinComponents.PHYSICS))
                        .then(setter("intensity", MannequinComponents.PHYSICS_BOUNCE))
                        .then(setter("momentum", MannequinComponents.PHYSICS_FLOPPY))
                        .then(setter("uniboob", MannequinComponents.PHYSICS_UNIBOOB)))
                    .then(setter("separation", MannequinComponents.BREAST_OFFSET_X))
                    .then(setter("height", MannequinComponents.BREAST_OFFSET_Y))
                    .then(setter("depth", MannequinComponents.BREAST_OFFSET_Z)))
                .then(setter("show_in_armor", MannequinComponents.SHOW_IN_ARMOR))
                .then(helper.literal("sounds")
                    .then(setter("enabled", MannequinComponents.HURT_SOUNDS))
                    .then(setter("pitch", MannequinComponents.VOICE_PITCH)))
                );
    }

    private Mannequin getMannequin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "mannequin");
        if(!(entity instanceof Mannequin mannequin)) {
            throw NOT_A_MANNEQUIN.create();
        }
        return mannequin;
    }

    private Pair<Avatar, AbstractAvatarConfigHolder> getCopyTarget(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity from = EntityArgument.getEntity(ctx, "from");
        if(!(from instanceof Avatar avatar)) {
            throw TARGET_NOT_AVATAR.create();
        }
        var fromConfig = WildfireServerAPI.getConfig(avatar);
        if(!(fromConfig instanceof AbstractAvatarConfigHolder avatarConfig)) {
            throw TARGET_NOT_AVATAR.create();
        }
        return Pair.of(avatar, avatarConfig);
    }

    private int editMannequin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Mannequin mannequin = getMannequin(ctx);

        if(WildfireNetworking.INSTANCE.canSendToPlayer(player, ClientboundEditMannequinPacket.TYPE)) {
            // does it make sense to send a feedback message here? I feel like the GUI opening
            // would be feedback enough in most cases
            WildfireNetworking.INSTANCE.sendToClient(player, new ClientboundEditMannequinPacket(mannequin));
        } else {
            ctx.getSource().sendFailure(WildfireLang.COMMAND_SERVER_NO_MOD_ON_CLIENT.translateColored(TextColor.RED));
        }

        return Command.SINGLE_SUCCESS;
    }

    private int copyAllOntoMannequin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Mannequin mannequin = getMannequin(ctx);
        MannequinConfigHolder config = WildfireServerAPI.mannequins().getOrCreate(mannequin);
        Pair<Avatar, AbstractAvatarConfigHolder> target = getCopyTarget(ctx);

        List<MannequinComponent<?>> copied = new ArrayList<>();
        for(var component : MannequinComponents.components()) {
            if(copy(config, target.right(), component)) {
                copied.add(component);
            }
        }

        if(copied.isEmpty()) {
            ctx.getSource().sendFailure(WildfireLang.COMMAND_MANNEQUIN_NO_CHANGES.translate(mannequin));
            return 0;
        }

        config.sync(mannequin, null);
        if(copied.size() == 1) {
            MannequinComponent<?> component = copied.getFirst();
            ctx.getSource().sendSuccess(
                () -> WildfireLang.COMMAND_MANNEQUIN_SET_VALUE.translate(mannequin, component.name(), component.formattedValue(config)),
                true);
            return 1;
        }

        ctx.getSource().sendSuccess(
            () -> WildfireLang.COMMAND_MANNEQUIN_COPIED_MULTIPLE_VALUES.translate(copied.size(), target.left(), mannequin),
            true);
        return copied.size();
    }

    private <T> boolean copy(MannequinConfigHolder config, AbstractAvatarConfigHolder from, MannequinComponent<T> component) {
        T value = component.rawValue(from);
        return component.update(config, value);
    }

    private <T> LiteralArgumentBuilder<CommandSourceStack> setter(final String name, final MannequinComponent<T> component) {
        return helper.literal(name)
            .then(component.argument(helper, "value")
                .executes(ctx -> {
                    Mannequin mannequin = getMannequin(ctx);
                    MannequinConfigHolder config = WildfireServerAPI.mannequins().getOrCreate(mannequin);

                    if(component.update(config, ctx, "value")) {
                        ctx.getSource().sendSuccess(
                            () -> WildfireLang.COMMAND_MANNEQUIN_SET_VALUE.translate(mannequin, component.name(), component.formattedValue(config)),
                            true);
                        config.sync(mannequin, null);
                        return Command.SINGLE_SUCCESS;
                    }

                    ctx.getSource().sendFailure(WildfireLang.COMMAND_MANNEQUIN_NO_CHANGES.translate(mannequin));
                    return 0;
                }));
    }
}
