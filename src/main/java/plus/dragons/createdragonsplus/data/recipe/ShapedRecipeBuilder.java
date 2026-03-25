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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;

public class ShapedRecipeBuilder extends BaseRecipeBuilder<net.minecraft.world.item.crafting.ShapedRecipe, ShapedRecipeBuilder> {
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private int width = 0;
    private final List<String> pattern = new ArrayList<>();
    private ItemStack result = ItemStack.EMPTY;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
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

    public ShapedRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        criteria.put(name, criterion);
        return this;
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
    public net.minecraft.world.item.crafting.ShapedRecipe build() {
        if (id == null) {
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        }
        return net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.getItem(), result.getCount())
                .getClass().cast(null); // not used directly
    }

    public void accept(Consumer<FinishedRecipe> output) {
        if (id == null) {
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        }
        ResourceLocation recipeId = this.directory == null ? id : new ResourceLocation(id.getNamespace(), this.directory + "/" + id.getPath());

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(recipeId))
                .requirements(net.minecraft.advancements.RequirementsStrategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        if (this.conditions.isEmpty()) {
            output.accept(new Result(recipeId, this.group, this.pattern, this.key, this.result, advancementBuilder, recipeId.withPrefix("recipes/")));
        } else {
            net.minecraftforge.common.crafting.ConditionalRecipe.Builder conditionalBuilder = net.minecraftforge.common.crafting.ConditionalRecipe.builder();
            for (ICondition condition : this.conditions) {
                conditionalBuilder.addCondition(condition);
            }
            conditionalBuilder.addRecipe(c -> c.accept(new Result(recipeId, this.group, this.pattern, this.key, this.result, advancementBuilder, recipeId.withPrefix("recipes/"))));
            conditionalBuilder.build(output, recipeId);
        }
    }

    private static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final ItemStack result;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        Result(ResourceLocation id, String group, List<String> pattern, Map<Character, Ingredient> key, ItemStack result, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.result = result;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            JsonArray patternArray = new JsonArray();
            for (String s : this.pattern) patternArray.add(s);
            json.add("pattern", patternArray);
            JsonObject keyJson = new JsonObject();
            for (Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                keyJson.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }
            json.add("key", keyJson);
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result.getItem()).toString());
            if (this.result.getCount() > 1) resultJson.addProperty("count", this.result.getCount());
            json.add("result", resultJson);
        }

        @Override
        public ResourceLocation getId() { return id; }

        @Override
        public RecipeSerializer<?> getType() { return RecipeSerializer.SHAPED_RECIPE; }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() { return advancement.serializeToJson(); }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() { return advancementId; }
    }
}
