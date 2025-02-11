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

package com.wildfire.main.config;

import com.wildfire.main.config.enums.ShowPlayerListMode;
import com.wildfire.main.config.enums.SyncVerbosity;
import com.wildfire.main.config.keys.BooleanConfigKey;
import com.wildfire.main.config.keys.EnumConfigKey;
import com.wildfire.main.config.keys.StringConfigKey;

public class GlobalConfig extends AbstractConfiguration {
    public static final GlobalConfig INSTANCE = new GlobalConfig();

    private GlobalConfig() {
        super(".", "wildfire_gender");
    }

    // this boolean is intended to only exist at runtime, and should not be saved or loaded in any persistent manner
    public static boolean RENDER_BREASTS = true;

    // region Cloud Sync

    public static final BooleanConfigKey FIRST_TIME_LOAD = new BooleanConfigKey("firstTimeLoad", true);
    public static final BooleanConfigKey CLOUD_SYNC_ENABLED = new BooleanConfigKey("cloud_sync", false);
    public static final BooleanConfigKey AUTOMATIC_CLOUD_SYNC = new BooleanConfigKey("sync_player_data", false);
    // see CloudSync#DEFAULT_CLOUD_URL for the actual default
    public static final StringConfigKey CLOUD_SERVER = new StringConfigKey("cloud_server", "");
    public static final EnumConfigKey<SyncVerbosity> SYNC_VERBOSITY = new EnumConfigKey<>("sync_log_verbosity", SyncVerbosity.DEFAULT, SyncVerbosity.BY_ID);

    // endregion

    public static final EnumConfigKey<ShowPlayerListMode> ALWAYS_SHOW_LIST = new EnumConfigKey<>("alwaysShowList", ShowPlayerListMode.MOD_UI_ONLY, ShowPlayerListMode.BY_ID);

    public static final BooleanConfigKey ARMOR_STAT = new BooleanConfigKey("armor_stat", true);

    // Development environment-only toggle; forces the client player's nametag to always render when set to true,
    // intended to allow for debugging nametag-related features.
    public static final BooleanConfigKey DEBUG_SHOW_NAMETAG = new BooleanConfigKey("showClientPlayerNametag", false);

    static {
        INSTANCE.setDefault(FIRST_TIME_LOAD);
        INSTANCE.setDefault(CLOUD_SYNC_ENABLED);
        INSTANCE.setDefault(AUTOMATIC_CLOUD_SYNC);
        INSTANCE.setDefault(CLOUD_SERVER);
        INSTANCE.setDefault(SYNC_VERBOSITY);
        INSTANCE.setDefault(ALWAYS_SHOW_LIST);
        INSTANCE.setDefault(ARMOR_STAT);
        // DEBUG_SHOW_NAMETAG is intentionally omitted as it's only ever used in a development environment
        if(!INSTANCE.exists()) {
            INSTANCE.save();
        }
    }
}
