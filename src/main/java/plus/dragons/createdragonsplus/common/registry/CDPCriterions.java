/*
 * Copyright (C) 2025  DragonsPlus
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
 *
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.common.registry;

import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.eventbus.api.IEventBus;
import plus.dragons.createdragonsplus.common.advancements.criterion.StatTrigger;

public class CDPCriterions {
    public static final Supplier<StatTrigger> STAT = new Supplier<>() {
        private StatTrigger instance;

        @Override
        public StatTrigger get() {
            if (instance == null) {
                instance = CriteriaTriggers.register(new StatTrigger());
            }
            return instance;
        }
    };

    public static void register(IEventBus modBus) {
        // Force initialization to register the trigger with CriteriaTriggers
        STAT.get();
    }
}
