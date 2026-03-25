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

package plus.dragons.createdragonsplus.common.kinetics.fan.ending;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Color;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class EndingFanProcessingType implements FanProcessingType {
    private final ResourceLocation createDNDRecipeTypeId;
    private final TagKey<Block> createDNDBlockCatalysts;
    private final TagKey<Fluid> createDNDFluidCatalysts;

    public EndingFanProcessingType() {
        this.createDNDRecipeTypeId = ModIntegration.CREATE_DND.asResource("dragon_breathing");
        this.createDNDBlockCatalysts = TagKey.create(Registries.BLOCK,
                ModIntegration.CREATE_DND.asResource("fan_processing_catalysts/dragon_breathing"));
        this.createDNDFluidCatalysts = TagKey.create(Registries.FLUID,
                ModIntegration.CREATE_DND.asResource("fan_processing_catalysts/dragon_breathing"));
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkEnding.get())
            return false;
        var fluidState = level.getFluidState(pos);
        if (fluidState.is(CDPFluids.MOD_TAGS.fanEndingCatalysts))
            return true;
        var state = level.getBlockState(pos);
        if (state.is(CDPBlocks.MOD_TAGS.fanEndingCatalysts))
            return true;
        if (ModIntegration.CREATE_DND.enabled()) {
            if (fluidState.is(createDNDFluidCatalysts))
                return true;
            if (state.is(createDNDBlockCatalysts))
                return true;
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 350; // Should be greater than Bulk Haunting and smaller than Bulk Washing
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (!CDPConfig.recipes().enableBulkEnding.get())
            return false;
        var recipeManager = level.getRecipeManager();
        RecipeWrapper input = createSingleItemWrapper(stack);
        if (recipeManager
                .getRecipeFor(CDPRecipes.ENDING.getType(), input, level)
                .isPresent())
            return true;
        return canProcessByCompatRecipe(createDNDRecipeTypeId, stack, level);
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        var recipeManager = level.getRecipeManager();
        RecipeWrapper input = createSingleItemWrapper(stack);
        return recipeManager
                .getRecipeFor(CDPRecipes.ENDING.getType(), input, level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe))
                .or(() -> processByCompatRecipe(createDNDRecipeTypeId, stack, level))
                .orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) == 0) {
            level.addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 1 / 8f, 0);
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors(0xB700D2, 0xDF00F9, random.nextFloat()));
        particleAccess.setAlpha(1f);
        if (random.nextFloat() < 1 / 32f)
            particleAccess.spawnExtraParticle(ParticleTypes.DRAGON_BREATH, 0f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        if (entity instanceof LivingEntity livingEntity && livingEntity.isAffectedByPotions() && entity.tickCount % 5 == 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
        }
    }

    @SuppressWarnings("unchecked")
    private boolean canProcessByCompatRecipe(ResourceLocation recipeTypeId, ItemStack stack, Level level) {
        if (!ModIntegration.CREATE_DND.enabled())
            return false;
        var recipeType = (RecipeType<ProcessingRecipe<?>>) ForgeRegistries.RECIPE_TYPES.getValue(recipeTypeId);
        if (recipeType == null)
            return false;
        RecipeWrapper wrapper = createSingleItemWrapper(stack);
        return level.getRecipeManager()
                .getRecipeFor(recipeType, wrapper, level)
                .isPresent();
    }

    @SuppressWarnings("unchecked")
    private Optional<List<ItemStack>> processByCompatRecipe(ResourceLocation recipeTypeId, ItemStack stack, Level level) {
        if (!ModIntegration.CREATE_DND.enabled())
            return Optional.empty();
        var recipeType = (RecipeType<ProcessingRecipe<?>>) ForgeRegistries.RECIPE_TYPES.getValue(recipeTypeId);
        if (recipeType == null)
            return Optional.empty();
        RecipeWrapper wrapper = createSingleItemWrapper(stack);
        return level.getRecipeManager()
                .getRecipeFor(recipeType, wrapper, level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe));
    }

    private static RecipeWrapper createSingleItemWrapper(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(1);
        handler.setStackInSlot(0, stack);
        return new RecipeWrapper(handler);
    }
}
