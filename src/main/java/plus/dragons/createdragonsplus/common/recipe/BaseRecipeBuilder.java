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

package plus.dragons.createdragonsplus.common.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRecipeBuilder<R extends Recipe<?>, B extends BaseRecipeBuilder<R, ?>> {
    protected final @Nullable String directory;
    protected final List<ICondition> conditions = new ArrayList<>();
    protected @Nullable ResourceLocation id;

    protected BaseRecipeBuilder(@Nullable String directory) {
        this.directory = directory;
    }

    protected abstract B builder();

    public abstract R build();

    public @Nullable String getDirectory() {
        return directory;
    }

    public @Nullable ResourceLocation getId() {
        return id;
    }

    public B withId(ResourceLocation id) {
        this.id = id;
        return builder();
    }

    public B withCondition(ICondition condition) {
        this.conditions.add(condition);
        return builder();
    }

    public final B withoutCondition(ICondition condition) {
        this.conditions.add(new NotCondition(condition));
        return builder();
    }

    public final B withAllCondition(ICondition... conditions) {
        Collections.addAll(this.conditions, conditions);
        return builder();
    }

    public final B withMod(String mod) {
        this.withCondition(new ModLoadedCondition(mod));
        return builder();
    }

    public final B withoutMod(String mod) {
        this.withoutCondition(new ModLoadedCondition(mod));
        return builder();
    }

    public void build(Consumer<FinishedRecipe> output) {
        R recipe = this.build();
        ResourceLocation recipeId = this.id != null ? this.id : recipe.getId();
        if (this.directory != null) {
            recipeId = new ResourceLocation(recipeId.getNamespace(), this.directory + "/" + recipeId.getPath());
        }
        // Delegate to the processing recipe builder's own build method if available
        // For Create's ProcessingRecipeBuilder, use its built-in save mechanism
    }
}
