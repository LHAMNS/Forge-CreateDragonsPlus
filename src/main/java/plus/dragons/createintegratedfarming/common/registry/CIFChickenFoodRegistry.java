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

package plus.dragons.createintegratedfarming.common.registry;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.util.valueproviders.ConstantInt;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodFluid;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodItem;

import java.util.Optional;

/**
 * Replacement for NeoForge DataMaps. Provides a simple registry for chicken food items and fluids.
 * In the NeoForge version, this was handled via CIFDataMaps using the data map system.
 */
public class CIFChickenFoodRegistry {
    private static final Map<Item, ChickenFoodItem> ITEM_FOODS = new HashMap<>();
    private static final Map<Fluid, ChickenFoodFluid> FLUID_FOODS = new HashMap<>();

    public static void register() {
        // Register default chicken foods - seeds
        registerItemFood(Items.WHEAT_SEEDS, new ChickenFoodItem(ConstantInt.of(600), ConstantInt.of(200), Optional.empty()));
        registerItemFood(Items.BEETROOT_SEEDS, new ChickenFoodItem(ConstantInt.of(600), ConstantInt.of(200), Optional.empty()));
        registerItemFood(Items.MELON_SEEDS, new ChickenFoodItem(ConstantInt.of(600), ConstantInt.of(200), Optional.empty()));
        registerItemFood(Items.PUMPKIN_SEEDS, new ChickenFoodItem(ConstantInt.of(600), ConstantInt.of(200), Optional.empty()));
        registerItemFood(Items.TORCHFLOWER_SEEDS, new ChickenFoodItem(ConstantInt.of(600), ConstantInt.of(200), Optional.empty()));
        registerItemFood(Items.PITCHER_POD, new ChickenFoodItem(ConstantInt.of(600), ConstantInt.of(200), Optional.empty()));
    }

    public static void registerItemFood(Item item, ChickenFoodItem food) {
        ITEM_FOODS.put(item, food);
    }

    public static void registerFluidFood(Fluid fluid, ChickenFoodFluid food) {
        FLUID_FOODS.put(fluid, food);
    }

    @Nullable
    public static ChickenFoodItem getItemFood(ItemStack stack) {
        return ITEM_FOODS.get(stack.getItem());
    }

    @Nullable
    public static ChickenFoodFluid getFluidFood(FluidStack fluid) {
        return FLUID_FOODS.get(fluid.getFluid());
    }
}
