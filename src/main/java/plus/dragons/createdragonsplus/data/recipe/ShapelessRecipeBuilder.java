/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;

public class ShapelessRecipeBuilder extends BaseShapelessRecipeBuilder<ShapelessRecipe, ShapelessRecipeBuilder> {
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";

    public ShapelessRecipeBuilder(@Nullable String directory) {
        super(directory);
    }

    public ShapelessRecipeBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public ShapelessRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    @Override
    protected ShapelessRecipeBuilder builder() {
        return this;
    }

    @Override
    public ShapelessRecipe build() {
        if (id == null) {
            throw new IllegalStateException("Recipe id is not set");
        }
        return new ShapelessRecipe(id, this.group, net.minecraft.data.recipes.RecipeBuilder.determineBookCategory(this.category), this.result, this.ingredients);
    }
}
