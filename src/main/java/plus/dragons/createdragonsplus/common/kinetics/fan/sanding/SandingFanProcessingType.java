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

package plus.dragons.createdragonsplus.common.kinetics.fan.sanding;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.kinetics.fan.DynamicParticleFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingFanProcessingType.ParticleData;
import plus.dragons.createdragonsplus.common.registry.CDPBlocks;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration;

public class SandingFanProcessingType implements DynamicParticleFanProcessingType<ParticleData> {
    private final ResourceLocation createDNDRecipeTypeId;
    private final TagKey<Block> createDNDBlockCatalysts;

    public SandingFanProcessingType() {
        this.createDNDRecipeTypeId = ModIntegration.CREATE_DND.asResource("sanding");
        this.createDNDBlockCatalysts = TagKey.create(Registries.BLOCK,
                ModIntegration.CREATE_DND.asResource("fan_processing_catalysts/sanding"));
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkSanding.get())
            return false;
        var state = level.getBlockState(pos);
        if (state.is(CDPBlocks.MOD_TAGS.fanSandingCatalysts)) return true;
        if (ModIntegration.CREATE_DND.enabled() && state.is(createDNDBlockCatalysts))
            return true;
        return false;
    }

    @Override
    public int getPriority() {
        return 700; // Should be greater than Bulk Freezing
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (!CDPConfig.recipes().enableBulkSanding.get())
            return false;
        var recipeManager = level.getRecipeManager();
        RecipeWrapper input = createSingleItemWrapper(stack);
        var recipe = recipeManager
                .getRecipeFor(CDPRecipes.SANDING.getType(), input, level)
                .or(() -> recipeManager.getRecipeFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), input, level));
        if (recipe.isPresent())
            return true;
        return canProcessByCompatRecipe(createDNDRecipeTypeId, stack, level);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        var recipeManager = level.getRecipeManager();
        RecipeWrapper input = createSingleItemWrapper(stack);
        return recipeManager
                .getRecipeFor(CDPRecipes.SANDING.getType(), input, level)
                .or(() -> recipeManager.getRecipeFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), input, level))
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe))
                .or(() -> processByCompatRecipe(createDNDRecipeTypeId, stack, level))
                .orElse(null);
    }

    @Override
    public @Nullable ParticleData getParticleDataAt(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        int color = 0xDBD3A0;
        if (state.getBlock() instanceof FallingBlock falling)
            color = falling.getDustColor(state, level, pos);
        return new ParticleData(state, color);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos, @Nullable ParticleData data) {
        if (level.random.nextInt(8) == 0) {
            var state = data == null ? Blocks.SAND.defaultBlockState() : data.state;
            level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state),
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 0, 0);
        }
        if (data != null)
            data.playSound(level, pos);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random, @Nullable ParticleData data) {
        int color = data == null ? 0xDBD3A0 : data.color;
        var state = data == null ? Blocks.SAND.defaultBlockState() : data.state;
        particleAccess.setColor(color);
        particleAccess.setAlpha(1f);
        if (random.nextInt(32) == 0)
            particleAccess.spawnExtraParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), 0);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        entity.extinguishFire();
    }

    public static class ParticleData {
        private final BlockState state;
        private final int color;
        private final Set<BlockPos> playedSoundPos = new ObjectArraySet<>();

        public ParticleData(BlockState state, int color) {
            this.state = state;
            this.color = color;
        }

        public void playSound(Level level, Vec3 pos) {
            if (level.getGameTime() % 7 == 0) {
                // Play sound only once per block pos
                if (playedSoundPos.add(BlockPos.containing(pos))) {
                    AllSoundEvents.SANDING_SHORT.playAt(level, pos,
                            0.3F + 0.1F * level.random.nextFloat(),
                            0.9F + 0.2F * level.random.nextFloat(),
                            true);
                }
            } else if (!playedSoundPos.isEmpty()) {
                playedSoundPos.clear();
            }
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
