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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringRecipe;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.data.internal.CDPLang;
import plus.dragons.createdragonsplus.integration.CompatUtility;
import plus.dragons.createdragonsplus.integration.jei.CDPJeiPlugin;
import plus.dragons.createdragonsplus.integration.jei.widget.FanProcessingIcon;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

public class FanColoringCategory extends ProcessingViaFanCategory<ColoringRecipe> {

    private FanColoringCategory(Info<ColoringRecipe> info) {
        super(info);
    }

    public static FanColoringCategory create() {
        var id = CDPCommon.asResource("fan_coloring");
        var title = CDPLang.description("recipe", id).component();
        var background = new EmptyBackground(178, 72);
        var icon = new Icon();
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.setHoverName(CDPLang.description("recipe", id, "fan").component().copy().withStyle(style -> style.withItalic(false)));
        var recipeType = new mezz.jei.api.recipe.RecipeType<>(id, ColoringRecipe.class);
        var info = new Info<>(recipeType, title, background, icon, FanColoringCategory::getAllRecipes, CompatUtility.catalystWithIndustryFan(catalyst));
        return new FanColoringCategory(info);
    }

    @Override
    public void draw(ColoringRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        renderWidgets(graphics, recipe, mouseX, mouseY);

        PoseStack matrixStack = graphics.pose();

        matrixStack.pushPose();
        translateFan(matrixStack);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-12.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        AnimatedKinetics.defaultBlockElement(AllPartialModels.ENCASED_FAN_INNER)
                .rotateBlock(180, 0, AnimatedKinetics.getCurrentAngle() * 16)
                .scale(SCALE)
                .render(graphics);

        AnimatedKinetics.defaultBlockElement(AllBlocks.ENCASED_FAN.getDefaultState())
                .rotateBlock(0, 180, 0)
                .atLocal(0, 0, 0)
                .scale(SCALE)
                .render(graphics);

        renderAttachedBlock(graphics, recipe.getColor());
        matrixStack.popPose();
    }

    protected void renderAttachedBlock(GuiGraphics graphics, DyeColor color) {
        GuiGameElement.of((Fluid) CDPFluids.DYES_BY_COLOR.get(color).getSource())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    @Override
    @Deprecated
    protected void renderAttachedBlock(GuiGraphics graphics) {}

    private static List<ColoringRecipe> getAllRecipes() {
        var manager = CDPJeiPlugin.getRecipeManager();
        var recipes = new ArrayList<>(manager.getAllRecipesFor(CDPRecipes.COLORING.getType()));
        recipes.sort(Comparator
                .<ColoringRecipe, DyeColor>comparing(ColoringRecipe::getColor, DyeColors.creativeModeTabOrder())
                .thenComparing(ColoringRecipe::getId));
        return recipes;
    }

    @FieldsNullabilityUnknownByDefault
    protected static class Icon extends FanProcessingIcon {
        private ItemStack[] catalystStacks;

        @Override
        protected ItemStack getCatalyst() {
            if (catalystStacks == null) {
                catalystStacks = Arrays.stream(DyeColors.ALL)
                        .map(CDPFluids.DYES_BY_COLOR::get)
                        .flatMap(entry -> entry.getBucket().stream())
                        .map(ItemStack::new)
                        .toArray(ItemStack[]::new);
            }
            return catalystStacks[(AnimationTickHolder.getTicks() / 20) % catalystStacks.length];
        }
    }
}
