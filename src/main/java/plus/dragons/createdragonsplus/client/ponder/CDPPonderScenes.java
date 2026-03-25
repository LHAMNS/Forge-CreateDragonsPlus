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

package plus.dragons.createdragonsplus.client.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import plus.dragons.createdragonsplus.client.ponder.scenes.CDPFanScenes;
import plus.dragons.createdragonsplus.client.ponder.scenes.SandingScenes;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class CDPPonderScenes {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CDPCommon.ID);

    public static void register() {
        HELPER.forComponents(AllBlocks.ENCASED_FAN)
                .addStoryBoard("bulk_coloring", CDPFanScenes::bulkColoring)
                .addStoryBoard("bulk_freezing", CDPFanScenes::bulkFreezing)
                .addStoryBoard("bulk_ending", CDPFanScenes::bulkEnding);

        if (ModIntegration.QUICKSAND.enabled() || ModIntegration.CREATE_DND.enabled() || BuiltInRegistries.BLOCK.getTag(CDPBlocks.MOD_TAGS.fanSandingCatalysts).isPresent()) {
            HELPER.forComponents(AllBlocks.ENCASED_FAN)
                    .addStoryBoard("bulk_sanding", SandingScenes::bulkSanding);
        }
    }
}
