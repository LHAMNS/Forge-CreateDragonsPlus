/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
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

package plus.dragons.createdragonsplus.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fml.ModList;

public enum ModIntegration {
    CREATE_GARNISHED(Constants.CREATE_GARNISHED),
    CREATE_DND(Constants.CREATE_DND),
    QUICKSAND(Constants.QUICKSAND);

    private final String id;

    ModIntegration(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public boolean enabled() {
        return ModList.get().isLoaded(id);
    }

    public ResourceLocation asResource(String path) {
        return new ResourceLocation(id, path);
    }

    public ModLoadedCondition condition() {
        return new ModLoadedCondition(id);
    }

    public void onConstructMod() {}

    public void onCommonSetup() {}

    @OnlyIn(Dist.CLIENT)
    public void onClientSetup() {}

    public static class Constants {
        public static final String CREATE_GARNISHED = "garnished";
        public static final String CREATE_DND = "dndesires";
        public static final String QUICKSAND = "quicksand";
    }
}
