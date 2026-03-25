/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.data.recipe;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;
import plus.dragons.createdragonsplus.data.recipe.integration.IntegrationResultRecipe;

public class ShapedRecipeBuilder extends BaseRecipeBuilder<ShapedRecipe, ShapedRecipeBuilder> {
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private int width = 0;
    private final List<String> pattern = new ArrayList<>();
    private ItemStack result = ItemStack.EMPTY;
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";
    private boolean showNotification = true;

    public ShapedRecipeBuilder(@Nullable String directory) {
        super(directory);
    }

    public ShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return define(symbol, Ingredient.of(tag));
    }

    public ShapedRecipeBuilder define(Character symbol, ItemLike item) {
        return define(symbol, Ingredient.of(item));
    }

    public ShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
        if (key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            key.put(symbol, ingredient);
            return this;
        }
    }

    public ShapedRecipeBuilder pattern(String line) {
        Preconditions.checkArgument(!line.isEmpty(), "Pattern line must not be empty");
        if (width == 0) {
            width = line.length();
        } else if (width != line.length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        pattern.add(line);
        return this;
    }

    public ShapedRecipeBuilder output(ItemLike item) {
        this.result = new ItemStack(item);
        return this;
    }

    public ShapedRecipeBuilder output(ItemLike item, int count) {
        this.result = new ItemStack(item, count);
        return this;
    }

    public ShapedRecipeBuilder output(ItemStack stack) {
        this.result = stack;
        return this;
    }

    public IntegrationResultRecipe.Builder output(ResourceLocation result) {
        return new IntegrationResultRecipe.Builder(this, this.result, result);
    }

    public ShapedRecipeBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public ShapedRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public ShapedRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    protected ShapedRecipeBuilder builder() {
        return this;
    }

    @Override
    public ShapedRecipe build() {
        if (id == null) {
            throw new IllegalStateException("Recipe id is not set");
        }
        var pattern = ShapedRecipePattern.of(this.key, this.pattern);
        return new ShapedRecipe(id, group, RecipeBuilder.determineBookCategory(category), pattern, result, showNotification);
    }
}
