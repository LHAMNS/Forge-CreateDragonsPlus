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
 */

package plus.dragons.createdragonsplus.common.registry;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import java.util.Collection;
import java.util.function.Supplier;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;

public class CDPItemAttributes {
    public static void register() {
        // In Create 0.5.1.f, item attributes are registered differently.
        // Custom fan processing attributes are handled through the FanProcessingType system.
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".freezable", "can be Frozen");
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".freezable.inverted", "cannot be Frozen");
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".sandable", "can be Sanded");
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".sandable.inverted", "cannot be Sanded");
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".endable", "can be Ended");
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".endable.inverted", "cannot be Ended");
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".stainable", "can be Stained");
        REGISTRATE.addRawLang("create.item_attributes." + CDPCommon.ID + ".stainable.inverted", "cannot be Stained");
    }
}
