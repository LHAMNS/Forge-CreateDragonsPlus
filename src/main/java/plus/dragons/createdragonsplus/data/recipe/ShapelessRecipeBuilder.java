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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipeBuilder extends BaseShapelessRecipeBuilder<ShapelessRecipe, ShapelessRecipeBuilder> {
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";

    public ShapelessRecipeBuilder(@Nullable String directory) {
        super(directory);
    }

    public ShapelessRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        criteria.put(name, criterion);
        return this;
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
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        }
        return new ShapelessRecipe(id, this.group, net.minecraft.world.item.crafting.CraftingBookCategory.MISC, this.result, this.ingredients);
    }

    public void accept(Consumer<FinishedRecipe> output) {
        if (id == null) {
            id = BuiltInRegistries.ITEM.getKey(result.getItem());
        }
        ResourceLocation recipeId = this.directory == null ? id : new ResourceLocation(id.getNamespace(), this.directory + "/" + id.getPath());

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        if (this.conditions.isEmpty()) {
            output.accept(new Result(recipeId, this.group, this.ingredients, this.result, advancementBuilder, recipeId.withPrefix("recipes/")));
        } else {
            ConditionalRecipe.Builder conditionalBuilder = ConditionalRecipe.builder();
            for (ICondition condition : this.conditions) {
                conditionalBuilder.addCondition(condition);
            }
            conditionalBuilder.addRecipe(c -> c.accept(new Result(recipeId, this.group, this.ingredients, this.result, advancementBuilder, recipeId.withPrefix("recipes/"))));
            conditionalBuilder.build(output, recipeId);
        }
    }

    private static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final java.util.List<Ingredient> ingredients;
        private final net.minecraft.world.item.ItemStack result;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        Result(ResourceLocation id, String group, java.util.List<Ingredient> ingredients, net.minecraft.world.item.ItemStack result, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.ingredients = ingredients;
            this.result = result;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            JsonArray ingredientArray = new JsonArray();
            for (Ingredient ingredient : this.ingredients) ingredientArray.add(ingredient.toJson());
            json.add("ingredients", ingredientArray);
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result.getItem()).toString());
            if (this.result.getCount() > 1) resultJson.addProperty("count", this.result.getCount());
            json.add("result", resultJson);
        }

        @Override
        public ResourceLocation getId() { return id; }

        @Override
        public RecipeSerializer<?> getType() { return RecipeSerializer.SHAPELESS_RECIPE; }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() { return advancement.serializeToJson(); }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() { return advancementId; }
    }
}
