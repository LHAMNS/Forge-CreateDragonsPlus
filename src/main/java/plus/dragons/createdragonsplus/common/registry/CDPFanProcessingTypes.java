/*
 * Copyright (C) 2025  DragonsPlus
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
 *
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.common.registry;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.common.kinetics.fan.coloring.ColoringFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.ending.EndingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.freezing.FreezingFanProcessingType;
import plus.dragons.createdragonsplus.common.kinetics.fan.sanding.SandingFanProcessingType;

public class CDPFanProcessingTypes {
    public static final Map<DyeColor, Supplier<ColoringFanProcessingType>> COLORING = Util.make(() -> {
        var builder = ImmutableMap.<DyeColor, Supplier<ColoringFanProcessingType>>builder();
        for (var color : DyeColors.ALL) {
            var type = new ColoringFanProcessingType(color);
            builder.put(color, () -> type);
        }
        return builder.build();
    });
    public static final Supplier<FreezingFanProcessingType> FREEZING = createLazy(FreezingFanProcessingType::new);
    public static final Supplier<SandingFanProcessingType> SANDING = createLazy(SandingFanProcessingType::new);
    public static final Supplier<EndingFanProcessingType> ENDING = createLazy(EndingFanProcessingType::new);

    private static <T extends FanProcessingType> Supplier<T> createLazy(Supplier<T> factory) {
        return new Supplier<>() {
            private T instance;

            @Override
            public T get() {
                if (instance == null) {
                    instance = factory.get();
                }
                return instance;
            }
        };
    }

    public static void register() {
        for (var entry : COLORING.entrySet()) {
            DyeColor color = entry.getKey();
            AllFanProcessingTypes.register(
                    new ResourceLocation(CDPCommon.ID, "coloring_" + color.getSerializedName()),
                    entry.getValue().get());
        }
        AllFanProcessingTypes.register(
                new ResourceLocation(CDPCommon.ID, "freezing"),
                FREEZING.get());
        AllFanProcessingTypes.register(
                new ResourceLocation(CDPCommon.ID, "sanding"),
                SANDING.get());
        AllFanProcessingTypes.register(
                new ResourceLocation(CDPCommon.ID, "ending"),
                ENDING.get());
    }
}
