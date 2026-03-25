/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.config.CDPConfig;

@Mixin(PotionMixingRecipes.class)
public class PotionMixingRecipesMixin {
    @Unique
    private static final List<MixingRecipe> FLUID_DRAGON_BREATH_RECIPES = new ArrayList<>();

    @WrapOperation(remap = false, method = "createRecipesImpl", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/fluids/potion/PotionMixingRecipes;createRecipe(Ljava/lang/String;Lnet/minecraft/world/item/crafting/Ingredient;Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/FluidStack;)Lcom/simibubi/create/content/kinetics/mixer/MixingRecipe;"))
    private static MixingRecipe createRecipesImpl$createDragonBreathFluidRecipe(String id, Ingredient ingredient, FluidStack fromFluid, FluidStack toFluid, Operation<MixingRecipe> original, @Local(name = "mixingRecipes") List<MixingRecipe> mixingRecipes) {
        if (CDPConfig.features().generateAutomaticBrewingRecipeForDragonBreathFluid.get()) {
            if (ingredient.test(new ItemStack(Items.DRAGON_BREATH))) {
                var recipeId = CDPCommon.asResource(id + "_using_dragon_breath_fluid");
                var recipe = new ProcessingRecipeBuilder<>(MixingRecipe::new, recipeId)
                        .require(CDPFluids.COMMON_TAGS.dragonBreath, 250)
                        .require(fromFluid.getFluid(), fromFluid.getAmount())
                        .output(toFluid)
                        .requiresHeat(HeatCondition.HEATED)
                        .build();
                FLUID_DRAGON_BREATH_RECIPES.add(recipe);
                mixingRecipes.add(recipe);
            }
        }
        return original.call(id, ingredient, fromFluid, toFluid);
    }

    @Inject(remap = false, method = "sortRecipesByItem(Ljava/util/List;)Ljava/util/Map;", at = @At("TAIL"))
    private static void sortRecipesByItem$sortDragonBreathFluidRecipes(List<MixingRecipe> all, CallbackInfoReturnable<Map<Item, List<MixingRecipe>>> cir) {
        var byItem = cir.getReturnValue();
        byItem.computeIfAbsent(Items.DRAGON_BREATH, ignored -> new ArrayList<>()).addAll(FLUID_DRAGON_BREATH_RECIPES);
    }
}
