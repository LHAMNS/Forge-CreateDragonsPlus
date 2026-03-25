/*
 * Copyright (C) 2025  DragonsPlus
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createdragonsplus.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class CDPConfig {
    private static CDPCommonConfig COMMON;
    private static CDPClientConfig CLIENT;
    private static CDPServerConfig SERVER;

    public static void register() {
        Pair<CDPCommonConfig, ForgeConfigSpec> commonPair = buildSpec(new CDPCommonConfig());
        COMMON = commonPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonPair.getRight());

        Pair<CDPClientConfig, ForgeConfigSpec> clientPair = buildSpec(new CDPClientConfig());
        CLIENT = clientPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientPair.getRight());

        Pair<CDPServerConfig, ForgeConfigSpec> serverPair = buildSpec(new CDPServerConfig());
        SERVER = serverPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverPair.getRight());
    }

    @SuppressWarnings("unchecked")
    private static <T extends com.simibubi.create.foundation.config.ConfigBase> Pair<T, ForgeConfigSpec> buildSpec(T config) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        config.registerAll(builder);
        return Pair.of(config, builder.build());
    }

    public static CDPCommonConfig common() {
        return COMMON;
    }

    public static CDPClientConfig client() {
        return CLIENT;
    }

    public static CDPServerConfig server() {
        return SERVER;
    }

    public static CDPFeaturesConfig features() {
        return COMMON.features;
    }

    public static CDPRecipesConfig recipes() {
        return SERVER.recipes;
    }
}
