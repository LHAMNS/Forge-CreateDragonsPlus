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

package plus.dragons.createdragonsplus.common.kinetics.fan.freezing;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.processing.freeze.BlockFreezer;
import plus.dragons.createdragonsplus.common.processing.freeze.FreezeCondition;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class FreezingFanProcessingType implements FanProcessingType {
    private final ResourceLocation createGarnishedRecipeTypeId;
    private final ResourceLocation createDNDRecipeTypeId;
    private final TagKey<Block> createGarnishedBlockCatalysts;
    private final TagKey<Block> createDNDBlockCatalysts;

    public FreezingFanProcessingType() {
        this.createGarnishedRecipeTypeId = ModIntegration.CREATE_GARNISHED.asResource("freezing");
        this.createDNDRecipeTypeId = ModIntegration.CREATE_DND.asResource("freezing");
        this.createGarnishedBlockCatalysts = TagKey.create(Registries.BLOCK,
                ModIntegration.CREATE_GARNISHED.asResource("fan_processing_catalysts/freezing"));
        this.createDNDBlockCatalysts = TagKey.create(Registries.BLOCK,
                ModIntegration.CREATE_DND.asResource("fan_processing_catalysts/freezing"));
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkFreezing.get())
            return false;
        var state = level.getBlockState(pos);
        float freeze = BlockFreezer.findFreeze(level, pos, state);
        if (freeze >= 0)
            return true;
        if (ModIntegration.CREATE_GARNISHED.enabled() && state.is(createGarnishedBlockCatalysts))
            return true;
        if (ModIntegration.CREATE_DND.enabled() && state.is(createDNDBlockCatalysts))
            return true;
        return false;
    }

    @Override
    public int getPriority() {
        return 600; // Should be greater than Bulk Coloring
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (!CDPConfig.recipes().enableBulkFreezing.get())
            return false;
        RecipeWrapper input = createSingleItemWrapper(stack);
        var recipe = level.getRecipeManager()
                .getRecipeFor(CDPRecipes.FREEZING.getType(), input, level);
        if (recipe.isPresent())
            return true;
        return canProcessByCompatRecipe(createGarnishedRecipeTypeId, ModIntegration.CREATE_GARNISHED, stack, level)
                || canProcessByCompatRecipe(createDNDRecipeTypeId, ModIntegration.CREATE_DND, stack, level);
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        RecipeWrapper input = createSingleItemWrapper(stack);
        return level.getRecipeManager()
                .getRecipeFor(CDPRecipes.FREEZING.getType(), input, level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe))
                .or(() -> {
                    var result = processByCompatRecipe(createGarnishedRecipeTypeId, ModIntegration.CREATE_GARNISHED, stack, level);
                    if (result.isEmpty())
                        result = processByCompatRecipe(createDNDRecipeTypeId, ModIntegration.CREATE_DND, stack, level);
                    return result;
                })
                .orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) == 0) {
            level.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 1 / 8f, 0);
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        int color = Color.mixColors(FreezeCondition.PASSIVE.getColor(), FreezeCondition.FROZEN.getColor(), random.nextFloat());
        particleAccess.setColor(color);
        particleAccess.setAlpha(1f);
        if (random.nextInt(32) == 0)
            particleAccess.spawnExtraParticle(ParticleTypes.SNOWFLAKE, 1 / 8f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        if (entity.canFreeze())
            entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen()) + 3);
        entity.extinguishFire();
    }

    @SuppressWarnings("unchecked")
    private boolean canProcessByCompatRecipe(ResourceLocation recipeTypeId, ModIntegration mod, ItemStack stack, Level level) {
        if (!mod.enabled())
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
    private Optional<List<ItemStack>> processByCompatRecipe(ResourceLocation recipeTypeId, ModIntegration mod, ItemStack stack, Level level) {
        if (!mod.enabled())
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
