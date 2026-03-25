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

package plus.dragons.createdragonsplus.integration.jei.category;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.data.internal.CDPLang;
import plus.dragons.createdragonsplus.integration.CompatUtility;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;

public class FanFreezingCategory extends ProcessingViaFanCategory<FreezingRecipe> {
    public static final mezz.jei.api.recipe.RecipeType<FreezingRecipe> TYPE =
            new mezz.jei.api.recipe.RecipeType<>(CDPRecipes.FREEZING.getId(), FreezingRecipe.class);

    private FanFreezingCategory(Info<FreezingRecipe> info) {
        super(info);
    }

    public static FanFreezingCategory create() {
        var id = CDPCommon.asResource("fan_freezing");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new DoubleItemIcon(AllItems.PROPELLER::asStack, Items.POWDER_SNOW_BUCKET::getDefaultInstance);
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.setHoverName(CDPLang.description("recipe", id, "fan").component().withStyle(style -> style.withItalic(false)));
        var info = new Info<>(TYPE, title, background, icon, FanFreezingCategory::getAllRecipes, CompatUtility.catalystWithIndustryFan(catalyst));
        return new FanFreezingCategory(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        GuiGameElement.of(Blocks.POWDER_SNOW.defaultBlockState())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    @SuppressWarnings("unchecked")
    private static List<FreezingRecipe> getAllRecipes() {
        var manager = CDPJeiPlugin.getRecipeManager();
        var recipes = new ArrayList<>(manager.getAllRecipesFor(CDPRecipes.FREEZING.getType()));
        ResourceLocation garnishedFreezingId = ModIntegration.CREATE_GARNISHED.asResource("freezing");
        RecipeType<?> garnishedType = ForgeRegistries.RECIPE_TYPES.getValue(garnishedFreezingId);
        if (garnishedType != null) {
            for (Recipe<?> recipe : manager.getAllRecipesFor(garnishedType)) {
                if (recipe instanceof ProcessingRecipe<?> pr) {
                    recipes.add(FreezingRecipe.builder(recipe.getId())
                            .withItemIngredients(pr.getIngredients())
                            .withItemOutputs(pr.getRollableResults().toArray(ProcessingOutput[]::new))
                            .build());
                }
            }
        }
        ResourceLocation dndFreezingId = ModIntegration.CREATE_DND.asResource("freezing");
        RecipeType<?> dndType = ForgeRegistries.RECIPE_TYPES.getValue(dndFreezingId);
        if (dndType != null) {
            for (Recipe<?> recipe : manager.getAllRecipesFor(dndType)) {
                if (recipe instanceof ProcessingRecipe<?> pr) {
                    recipes.add(FreezingRecipe.builder(recipe.getId())
                            .withItemIngredients(pr.getIngredients())
                            .withItemOutputs(pr.getRollableResults().toArray(ProcessingOutput[]::new))
                            .build());
                }
            }
        }
        return recipes;
    }
}
