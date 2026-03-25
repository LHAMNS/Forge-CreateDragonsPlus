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

package plus.dragons.createdragonsplus.data.recipe.integration;

import java.util.function.Consumer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.recipe.BaseRecipeBuilder;

public final class IntegrationResultRecipe implements Recipe<Container> {
    private final Recipe<?> delegate;
    private final IntegrationResult result;

    public IntegrationResultRecipe(Recipe<?> delegate, ItemStack delegateResult, ResourceLocation result) {
        this.delegate = delegate;
        this.result = new IntegrationResult(delegateResult, result);
    }

    public Recipe<?> getDelegate() {
        return delegate;
    }

    public IntegrationResult getIntegrationResult() {
        return result;
    }

    @Override
    public boolean matches(Container container, Level level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceLocation getId() {
        return delegate.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return delegate.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return delegate.getType();
    }

    public static class Builder extends BaseRecipeBuilder<IntegrationResultRecipe, Builder> {
        private final BaseRecipeBuilder<?, ?> delegate;
        private final ItemStack delegateResult;
        private final ResourceLocation result;

        public Builder(BaseRecipeBuilder<?, ?> delegate, ItemStack delegateResult, ResourceLocation result) {
            super(delegate.getDirectory());
            this.delegate = delegate;
            this.delegateResult = delegateResult;
            this.result = result;
            if (delegate.getId() == null) {
                delegate.withId(result);
            }
        }

        @Override
        protected Builder builder() {
            return this;
        }

        @Override
        public IntegrationResultRecipe build() {
            var recipe = delegate.build();
            return new IntegrationResultRecipe(recipe, delegateResult, result);
        }

        @Override
        public @Nullable ResourceLocation getId() {
            return delegate.getId();
        }

        @Override
        public @Nullable String getDirectory() {
            return delegate.getDirectory();
        }

        @Override
        public Builder withId(ResourceLocation id) {
            delegate.withId(id);
            return this;
        }

        @Override
        public Builder withCondition(ICondition condition) {
            delegate.withCondition(condition);
            return this;
        }
    }
}
