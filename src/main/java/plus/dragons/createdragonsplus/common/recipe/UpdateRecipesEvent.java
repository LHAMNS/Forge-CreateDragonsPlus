/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.common.recipe;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.mixin.minecraft.RecipeManagerAccessor;

/**
 * Fired when the {@link RecipeManager} has reloaded and is about sync the recipes from the server to the client.
 *
 * <p>This event is fired on the game event bus,
 * only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 */
public class UpdateRecipesEvent extends Event {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RecipeManager recipeManager;
    private final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> byType;
    private final Map<ResourceLocation, Recipe<?>> byName;
    private int added;
    private int removed;

    @Internal
    public UpdateRecipesEvent(RecipeManager recipeManager, Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> byType, Map<ResourceLocation, Recipe<?>> byName) {
        this.recipeManager = recipeManager;
        this.byType = byType;
        this.byName = byName;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public void addRecipe(Recipe<?> recipe) {
        byType.computeIfAbsent(recipe.getType(), k -> new HashMap<>()).put(recipe.getId(), recipe);
        byName.put(recipe.getId(), recipe);
        added++;
    }

    public void removeRecipe(Recipe<?> recipe) {
        Map<ResourceLocation, Recipe<?>> typeMap = byType.get(recipe.getType());
        if (typeMap != null) {
            typeMap.remove(recipe.getId());
        }
        byName.remove(recipe.getId());
        removed++;
    }

    @Internal
    public void apply() {
        ((RecipeManagerAccessor) recipeManager).setRecipes(byType);
        ((RecipeManagerAccessor) recipeManager).setByName(byName);
        LOGGER.debug("Added {} recipes to RecipeManager", added);
        LOGGER.debug("Removed {} recipes from RecipeManager", removed);
    }
}
