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

package plus.dragons.createdragonsplus.data.internal;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.recipe.UpdateRecipesEvent;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.data.recipe.CreateRecipeBuilders;

@EventBusSubscriber
public class CDPRuntimeRecipeProvider extends RecipeProvider {
    public CDPRuntimeRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
        if (CDPConfig.features().generateSandPaperPolishingRecipeForPolishedBlocks.get()) {
            buildPolishedBlockRecipes(output);
        }
    }

    private static void buildPolishedBlockRecipes(Consumer<FinishedRecipe> output) {
        BuiltInRegistries.BLOCK.holders()
                .filter(holder -> holder.key().location().getPath().contains("polished_"))
                .forEach(holder -> {
                    var polishedId = holder.key().location();
                    var baseId = new ResourceLocation(polishedId.getNamespace(), polishedId.getPath().replace("polished_", ""));
                    if (!BuiltInRegistries.BLOCK.containsKey(baseId))
                        return;
                    var polishedItem = holder.value().asItem();
                    var baseBlock = BuiltInRegistries.BLOCK.getHolder(baseId);
                    if (baseBlock.isEmpty() || baseBlock.get().is(CDPBlocks.MOD_TAGS.notApplicablePolishing))
                        return;
                    var baseItem = baseBlock.get().value().asItem();
                    if (polishedItem == Items.AIR || baseItem == Items.AIR)
                        return;
                    var recipeId = CDPCommon.asResource(baseId.toString().replace(':', '/'));
                    CreateRecipeBuilders.polishing(recipeId)
                            .require(baseItem)
                            .output(polishedItem)
                            .build(output);
                });
    }

    @SubscribeEvent
    public static void buildRecipesForUpdate(final UpdateRecipesEvent event) {
        if (CDPConfig.features().generateSandPaperPolishingRecipeForPolishedBlocks.get()) {
            BuiltInRegistries.BLOCK.holders()
                    .filter(holder -> holder.key().location().getPath().contains("polished_"))
                    .forEach(holder -> {
                        var polishedId = holder.key().location();
                        var baseId = new ResourceLocation(polishedId.getNamespace(), polishedId.getPath().replace("polished_", ""));
                        if (!BuiltInRegistries.BLOCK.containsKey(baseId))
                            return;
                        var polishedItem = holder.value().asItem();
                        var baseBlock = BuiltInRegistries.BLOCK.getHolder(baseId);
                        if (baseBlock.isEmpty() || baseBlock.get().is(CDPBlocks.MOD_TAGS.notApplicablePolishing))
                            return;
                        var baseItem = baseBlock.get().value().asItem();
                        if (polishedItem == Items.AIR || baseItem == Items.AIR)
                            return;
                        var recipeId = CDPCommon.asResource(baseId.toString().replace(':', '/'));
                        var recipe = CreateRecipeBuilders.polishing(recipeId)
                                .require(baseItem)
                                .output(polishedItem)
                                .build();
                        event.addRecipe(recipeId, recipe);
                    });
        }
    }
}
