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

package plus.dragons.createdragonsplus.data.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;

public class VanillaRecipeBuilders {
    public static ShapedRecipeBuilder shaped() {
        return new ShapedRecipeBuilder("crafting");
    }

    public static ShapelessRecipeBuilder shapeless() {
        return new ShapelessRecipeBuilder("crafting");
    }

    public static SingleItemRecipeBuilder stonecutting() {
        return new SingleItemRecipeBuilder("stonecutting", RecipeSerializer.STONECUTTER);
    }

    public static CookingRecipeBuilder<?> smelting() {
        return new CookingRecipeBuilder<>("smelting", RecipeSerializer.SMELTING_RECIPE, 200);
    }

    public static CookingRecipeBuilder<?> blasting() {
        return new CookingRecipeBuilder<>("blasting", RecipeSerializer.BLASTING_RECIPE, 100);
    }

    public static CookingRecipeBuilder<?> smoking() {
        return new CookingRecipeBuilder<>("smoking", RecipeSerializer.SMOKING_RECIPE, 100);
    }

    public static CookingRecipeBuilder<?> campfire() {
        return new CookingRecipeBuilder<>("smoking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600);
    }
}
