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

package com.wildfire.common.networking.packets.mannequins;

import com.wildfire.api.server.WildfireServerAPI;
import com.wildfire.common.WildfireGender;
import com.wildfire.common.entities.avatars.AvatarConfig;
import com.wildfire.common.entities.avatars.MannequinConfigHolder;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.Mannequin;

public record ServerboundMannequinDataPacket(UUID uuid, AvatarConfig config) implements CustomPacketPayload {
    public static final Type<ServerboundMannequinDataPacket> TYPE = WildfireGender.serverBoundPacket("mannequin_data");
    public static final StreamCodec<ByteBuf, ServerboundMannequinDataPacket> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundMannequinDataPacket::uuid,
        // note that this is intentionally using the full avatar codec over using the compact codec;
        // this is because if the player sets the mannequin's gender to male through the GUI (e.g. to cycle from other to female),
        // the mannequin's config will be fully cleared when the server syncs their changes back to them.
        // this would ideally be fixed by just updating individual components for a mannequin, but doing so would
        // also require additional work to support this in the GUI. so, in the meantime, just be a little
        // less efficient and send the full config.
        AvatarConfig.STREAM_CODEC, ServerboundMannequinDataPacket::config,
        ServerboundMannequinDataPacket::new
    );

    public ServerboundMannequinDataPacket(MannequinConfigHolder holder) {
        this(holder.uuid, holder.config());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer player) {
        var entity = player.level().getEntity(uuid());
        if(!(entity instanceof Mannequin mannequin)) {
            return;
        }
        var config = WildfireServerAPI.mannequins().getOrCreate(mannequin);
        if(!config.canEdit(player)) {
            // TODO send some kind of feedback for rejected edits
            return;
        }
        config.setConfigAndSync(mannequin, config(), player);
    }
}
