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

package plus.dragons.createdragonsplus.common.kinetics.fan.coloring;

import static plus.dragons.createdragonsplus.common.CDPCommon.PERSISTENT_DATA_KEY;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.common.registry.CDPFluids;
import plus.dragons.createdragonsplus.common.registry.CDPItems;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.util.PersistentDataHelper;

public class ColoringFanProcessingType implements FanProcessingType {
    private final DyeColor color;
    private final Vector3f rgb;

    public ColoringFanProcessingType(DyeColor color) {
        this.color = color;
        this.rgb = new Color(this.color.getTextureDiffuseColor()).asVectorF();
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!CDPConfig.recipes().enableBulkColoring.get())
            return false;
        var fluidState = level.getFluidState(pos);
        if (!fluidState.isEmpty()) {
            var fluid = fluidState.getType();
            var dyeFluidEntry = CDPFluids.DYES_BY_COLOR.get(this.color);
            if (dyeFluidEntry != null && (fluid == dyeFluidEntry.get() || fluid == dyeFluidEntry.getSource())) {
                return true;
            }
            var dyeTag = CDPFluids.COMMON_TAGS.dyesByColor.get(this.color);
            if (dyeTag != null && fluidState.is(dyeTag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 500;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (!CDPConfig.recipes().enableBulkColoring.get())
            return false;
        var recipe = level.getRecipeManager()
                .getRecipeFor(CDPRecipes.COLORING.getType(), new ColoringRecipeInput(this.color, stack), level);
        if (recipe.isPresent())
            return true;
        return this.processByCrafting(stack, level).isPresent();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(CDPRecipes.COLORING.getType(), new ColoringRecipeInput(this.color, stack), level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe))
                .or(() -> processByCrafting(stack, level)
                        .map(result -> ItemHelper.multipliedOutput(stack, result)))
                .orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) == 0) {
            level.addParticle(new DustParticleOptions(this.rgb, 2),
                    pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f,
                    0, 1 / 8f, 0);
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(this.color.getTextureDiffuseColor());
        particleAccess.setAlpha(1f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide)
            return;
        if (entity instanceof LivingEntity livingEntity)
            this.applyColoring(livingEntity, level);
        if (entity instanceof EnderMan || entity.getType() == EntityType.SNOW_GOLEM || entity.getType() == EntityType.BLAZE) {
            entity.hurt(entity.damageSources().drown(), 2);
        }
        if (entity.isOnFire()) {
            entity.clearFire();
            level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    SoundSource.NEUTRAL, 0.7F, 1.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        }
    }

    private Optional<ItemStack> processByCrafting(ItemStack stack, Level level) {
        if (stack.is(CDPItems.MOD_TAGS.notApplicableColoring))
            return Optional.empty();
        CraftingContainer input = createCraftingContainer(2, 1,
                stack, new ItemStack(DyeItem.byColor(this.color)));
        var optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        if (optional.isPresent()) {
            var recipe = optional.get();
            var result = recipe.assemble(input, level.registryAccess());
            return result.getCount() == 1 ? Optional.of(result) : Optional.empty();
        }
        ItemStack[] items = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            items[i] = i == 4 ? new ItemStack(DyeItem.byColor(this.color)) : stack.copy();
        }
        input = createCraftingContainer(3, 3, items);
        optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        if (optional.isPresent()) {
            var recipe = optional.get();
            var result = recipe.assemble(input, level.registryAccess());
            if (result.getCount() != 8)
                return Optional.empty();
            result.setCount(1);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public void applyColoring(LivingEntity entity, Level level) {
        if (processColoring(entity)) {
            if (entity instanceof Sheep sheep) {
                sheep.setColor(this.color);
            } else if (entity instanceof Shulker shulker) {
                shulker.getEntityData().set(Shulker.DATA_COLOR_ID, (byte) this.color.getId());
            } else if (entity instanceof Cat cat) {
                cat.setCollarColor(this.color);
            } else if (entity instanceof Wolf wolf) {
                wolf.setCollarColor(this.color);
            }
            for (var slot : EquipmentSlot.values()) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (stack.isEmpty())
                    continue;
                this.applyColoring(stack, level).ifPresent(it -> {
                    it.setCount(stack.getCount());
                    entity.setItemSlot(slot, it);
                });
            }
        }
    }

    private boolean processColoring(LivingEntity entity) {
        CompoundTag nbt = PersistentDataHelper.getOrCreate(entity.getPersistentData(), PERSISTENT_DATA_KEY, "Coloring");
        int sinceLastProcess = 0;
        if (!(nbt.contains("Color", Tag.TAG_STRING) && nbt.getString("Color").equals(this.color.getName()))) {
            nbt.putString("Color", this.color.getName());
            nbt.remove("Time");
        } else if (nbt.contains("LastProcess", Tag.TAG_INT)) {
            int lastProcess = nbt.getInt("LastProcess");
            sinceLastProcess = entity.tickCount - lastProcess - 1;
        }
        nbt.putInt("LastProcess", entity.tickCount);
        int processingTime = AllConfigs.server().kinetics.fanProcessingTime.get();
        if (!nbt.contains("Time", Tag.TAG_INT) || sinceLastProcess < 0) {
            nbt.putInt("Time", processingTime);
            return false;
        }
        int time = nbt.getInt("Time") + sinceLastProcess;
        if (time == 0) {
            nbt.remove("Color");
            nbt.remove("LastProcess");
            nbt.remove("Time");
            return true;
        }
        nbt.putInt("Time", Math.min(processingTime, time - 1));
        return false;
    }

    private Optional<ItemStack> applyColoring(ItemStack stack, Level level) {
        var coloringInput = new ColoringRecipeInput(this.color, stack);
        var coloringRecipe = level.getRecipeManager().getRecipeFor(CDPRecipes.COLORING.getType(), coloringInput, level);
        if (coloringRecipe.isPresent()) {
            ItemStack result = coloringRecipe.get().assemble(coloringInput, level.registryAccess());
            return Optional.of(result);
        }
        return this.processByCrafting(stack, level);
    }

    private static CraftingContainer createCraftingContainer(int width, int height, ItemStack... items) {
        TransientCraftingContainer container = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(net.minecraft.world.entity.player.Player player) {
                return false;
            }
        }, width, height);
        for (int i = 0; i < items.length && i < container.getContainerSize(); i++) {
            container.setItem(i, items[i]);
        }
        return container;
    }
}
