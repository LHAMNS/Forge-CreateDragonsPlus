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

package plus.dragons.createdragonsplus.common.kinetics.fan.coloring;

import net.minecraft.world.item.DyeColor;

/**
 * In Create 0.5.1.f (Forge 1.20.1), there is no separate ProcessingRecipeParams subclass system.
 * Extra recipe data like DyeColor is stored directly on the recipe via writeAdditional/readAdditional
 * and writeExtra/readExtra. This class is retained as a simple data holder for convenience.
 */
public class ColoringRecipeParams {
    protected DyeColor color;

    public ColoringRecipeParams() {
        this.color = DyeColor.WHITE;
    }

    public ColoringRecipeParams(DyeColor color) {
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }
}
