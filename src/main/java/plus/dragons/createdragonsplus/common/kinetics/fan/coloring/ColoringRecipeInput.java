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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * A wrapper for coloring recipe input that includes a DyeColor and a single ItemStack.
 * In Forge 1.20.1, RecipeInput does not exist, so we extend RecipeWrapper.
 */
public class ColoringRecipeInput extends RecipeWrapper {
    private final DyeColor color;

    public ColoringRecipeInput(DyeColor color, ItemStack item) {
        super(createHandler(item));
        this.color = color;
    }

    private static ItemStackHandler createHandler(ItemStack item) {
        ItemStackHandler handler = new ItemStackHandler(1);
        handler.setStackInSlot(0, item);
        return handler;
    }

    public DyeColor color() {
        return color;
    }

    public ItemStack item() {
        return getItem(0);
    }
}
