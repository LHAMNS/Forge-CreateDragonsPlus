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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class CookingRecipeBuilder<R extends AbstractCookingRecipe> extends BaseSingleItemRecipeBuilder<R, CookingRecipeBuilder<R>> {
    private final RecipeSerializer<R> serializer;
    private float experience;
    private int cookingTime;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    private CookingBookCategory category = CookingBookCategory.MISC;
    private String group = "";

    @SuppressWarnings("unchecked")
    public CookingRecipeBuilder(@Nullable String directory, RecipeSerializer<? extends R> serializer, int cookingTime) {
        super(directory);
        this.serializer = (RecipeSerializer<R>) serializer;
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

    public CookingRecipeBuilder<R> unlockedBy(String name, CriterionTriggerInstance criterion) {
        criteria.put(name, criterion);
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
        throw new UnsupportedOperationException("Use accept(Consumer<FinishedRecipe>) instead");
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

        output.accept(new Result(recipeId, this.serializer, this.group, this.ingredient, this.result, this.experience, this.cookingTime, advancementBuilder, recipeId.withPrefix("recipes/")));
    }

    private static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final RecipeSerializer<?> serializer;
        private final String group;
        private final net.minecraft.world.item.crafting.Ingredient ingredient;
        private final net.minecraft.world.item.ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        Result(ResourceLocation id, RecipeSerializer<?> serializer, String group, net.minecraft.world.item.crafting.Ingredient ingredient, net.minecraft.world.item.ItemStack result, float experience, int cookingTime, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.serializer = serializer;
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            json.add("ingredient", this.ingredient.toJson());
            json.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result.getItem()).toString());
            json.addProperty("experience", this.experience);
            json.addProperty("cookingtime", this.cookingTime);
        }

        @Override
        public ResourceLocation getId() { return id; }

        @Override
        public RecipeSerializer<?> getType() { return serializer; }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() { return advancement.serializeToJson(); }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() { return advancementId; }
    }
}
