/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.data.recipe;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import org.jetbrains.annotations.Nullable;

public class CookingRecipeBuilder<R extends AbstractCookingRecipe> extends BaseSingleItemRecipeBuilder<R, CookingRecipeBuilder<R>> {
    private final AbstractCookingRecipe.Factory<R> factory;
    private float experience;
    private int cookingTime;
    private CookingBookCategory category = CookingBookCategory.MISC;
    private String group = "";

    protected CookingRecipeBuilder(@Nullable String directory, AbstractCookingRecipe.Factory<R> factory) {
        super(directory);
        this.factory = factory;
    }

    public CookingRecipeBuilder(@Nullable String directory, AbstractCookingRecipe.Factory<R> factory, int cookingTime) {
        super(directory);
        this.factory = factory;
        this.cookingTime = cookingTime;
    }

    public CookingRecipeBuilder<R> experience(float experience) {
        this.experience = experience;
        return this;
    }

    public CookingRecipeBuilder<R> cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public CookingRecipeBuilder<R> category(CookingBookCategory category) {
        this.category = category;
        return this;
    }

    public CookingRecipeBuilder<R> group(String group) {
        this.group = group;
        return this;
    }

    @Override
    protected CookingRecipeBuilder<R> builder() {
        return this;
    }

    @Override
    public R build() {
        if (id == null) {
            throw new IllegalStateException("Recipe id is not set");
        }
        return factory.create(id, group, category, ingredient, result, experience, cookingTime);
    }
}
