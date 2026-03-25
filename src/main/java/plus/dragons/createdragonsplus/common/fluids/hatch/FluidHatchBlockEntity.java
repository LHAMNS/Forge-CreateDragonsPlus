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

package plus.dragons.createdragonsplus.common.fluids.hatch;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
public class FluidHatchBlockEntity extends SmartBlockEntity {
    public FilteringBehaviour filtering;

    public FluidHatchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(filtering = new FilteringBehaviour(this, new FluidHatchFilterSlot()).forFluids());
    }

    /**
     * A simple filter slot positioned at the center of the block face.
     * Replaces HatchFilterSlot which does not exist in Create 0.5.1.f.
     */
    private static class FluidHatchFilterSlot extends ValueBoxTransform {
        @Override
        protected Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(FluidHatchBlock.FACING);
            return new Vec3(0.5, 0.5, 0.5).add(Vec3.atLowerCornerOf(facing.getOpposite().getNormal()).scale(0.5));
        }

        @Override
        protected boolean testHit(BlockState state, Vec3 localHit) {
            return true;
        }
    }
}
