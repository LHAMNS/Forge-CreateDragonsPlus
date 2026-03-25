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

package plus.dragons.createdragonsplus.client.model;

/**
 * Partial models registration placeholder.
 * <p>
 * In NeoForge 1.21.1 with Create 6.0.x, this class registered package partial models
 * via {@code AllPartialModels.PACKAGES} and {@code AllPartialModels.PACKAGE_RIGGING}.
 * The packages system does not exist in Create 0.5.1.f for Forge 1.20.1,
 * so this class is intentionally left empty.
 * </p>
 */
public class CDPPartialModels {
    public static void register() {
        // No-op: The Create 6.0.x package system (AllPartialModels.PACKAGES,
        // AllPartialModels.PACKAGE_RIGGING) does not exist in Create 0.5.1.f.
        // Package-related partial models are not applicable in this version.
    }
}
