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
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.data.internal.CDPLang;
import plus.dragons.createdragonsplus.integration.CompatUtility;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;
import plus.dragons.createdragonsplus.integration.jei.widget.FanProcessingIcon;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
public class FanSandingCategory extends ProcessingViaFanCategory<SandingRecipe> {
    public static final mezz.jei.api.recipe.RecipeType<SandingRecipe> TYPE =
            new mezz.jei.api.recipe.RecipeType<>(CDPRecipes.SANDING.getId(), SandingRecipe.class);

    private FanSandingCategory(Info<SandingRecipe> info) {
        super(info);
    }

    public static FanSandingCategory create() {
        var id = CDPCommon.asResource("fan_sanding");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new Icon();
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.setHoverName(CDPLang.description("recipe", id, "fan").component().withStyle(style -> style.withItalic(false)));
        var info = new Info<>(TYPE, title, background, icon, FanSandingCategory::getAllRecipes, CompatUtility.catalystWithIndustryFan(catalyst));
        return new FanSandingCategory(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        TagKey<Block> tag = CDPBlocks.MOD_TAGS.fanSandingCatalysts;
        List<Block> blocks = new ArrayList<>();
        ForgeRegistries.BLOCKS.tags().getTag(tag).forEach(blocks::add);
        if (blocks.isEmpty()) {
            TagKey<Block> dndTag = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("dndesires", "fan_processing_catalysts/sanding"));
            ForgeRegistries.BLOCKS.tags().getTag(dndTag).forEach(blocks::add);
        }
        if (blocks.isEmpty())
            return;
        BlockState[] states = blocks.stream().map(Block::defaultBlockState).toArray(BlockState[]::new);
        GuiGameElement.of(states[(AnimationTickHolder.getTicks() / 20) % states.length])
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    @Override
    public boolean isHandled(SandingRecipe recipe) {
        TagKey<Block> tag = CDPBlocks.MOD_TAGS.fanSandingCatalysts;
        boolean hasBlocks = false;
        for (var ignored : ForgeRegistries.BLOCKS.tags().getTag(tag)) {
            hasBlocks = true;
            break;
        }
        return hasBlocks || ModIntegration.CREATE_DND.enabled();
    }

    @SuppressWarnings("unchecked")
    private static List<SandingRecipe> getAllRecipes() {
        var level = CDPJeiPlugin.getLevel();
        var manager = CDPJeiPlugin.getRecipeManager();
        var recipes = new ArrayList<>(manager.getAllRecipesFor(CDPRecipes.SANDING.getType()));
        manager.getAllRecipesFor(AllRecipeTypes.SANDPAPER_POLISHING.getType())
                .stream()
                .filter(r -> r instanceof SandPaperPolishingRecipe)
                .map(r -> (SandPaperPolishingRecipe) r)
                .filter(SandPaperPolishingRecipe::canBeAutomated)
                .map(SandingRecipe::convertSandPaperPolishing)
                .forEach(recipes::add);
        ResourceLocation dndSandingId = ModIntegration.CREATE_DND.asResource("sanding");
        RecipeType<?> dndType = ForgeRegistries.RECIPE_TYPES.getValue(dndSandingId);
        if (dndType != null) {
            for (Recipe<?> recipe : manager.getAllRecipesFor(dndType)) {
                if (recipe instanceof ProcessingRecipe<?> pr) {
                    recipes.add(SandingRecipe.builder(recipe.getId())
                            .withItemIngredients(pr.getIngredients())
                            .withItemOutputs(pr.getRollableResults().toArray(ProcessingOutput[]::new))
                            .build());
                }
            }
        }
        return recipes;
    }

    protected static class Icon extends FanProcessingIcon {
        private ItemStack[] catalystStacks;

        @Override
        protected ItemStack getCatalyst() {
            TagKey<Block> tag = CDPBlocks.MOD_TAGS.fanSandingCatalysts;
            List<Block> blocks = new ArrayList<>();
            ForgeRegistries.BLOCKS.tags().getTag(tag).forEach(blocks::add);
            if (blocks.isEmpty())
                return ItemStack.EMPTY;
            if (catalystStacks == null || catalystStacks.length != blocks.size()) {
                catalystStacks = blocks.stream()
                        .map(ItemStack::new)
                        .toArray(ItemStack[]::new);
            }
            if (catalystStacks.length == 0)
                return ItemStack.EMPTY;
            return catalystStacks[(AnimationTickHolder.getTicks() / 20) % catalystStacks.length];
        }
    }
}
