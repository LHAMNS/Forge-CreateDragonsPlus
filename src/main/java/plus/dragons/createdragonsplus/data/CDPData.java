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

package plus.dragons.createdragonsplus.data;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.data.loading.DatagenModLoader;
import plus.dragons.createdragonsplus.client.ponder.CDPPonderPlugin;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.data.internal.CDPRecipeProvider;

public class CDPData {
    public CDPData(IEventBus modBus) {
        if (!DatagenModLoader.isRunningDataGen())
            return;
        REGISTRATE.registerBuiltinLocalization("interface");
        REGISTRATE.registerBuiltinLocalization("tooltips");
        REGISTRATE.registerPonderLocalization(CDPPonderPlugin::new);
        REGISTRATE.registerForeignLocalization();
        modBus.register(this);
    }

    @SubscribeEvent
    public void generate(final GatherDataEvent event) {
        var server = event.includeServer();
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var registries = event.getLookupProvider();
        generator.addProvider(server, new CDPRecipeProvider(output, registries));
    }
}
