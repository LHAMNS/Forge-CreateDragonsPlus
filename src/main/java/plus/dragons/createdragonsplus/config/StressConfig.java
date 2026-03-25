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

package plus.dragons.createdragonsplus.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = ConfigBase.class, source = "create", license = "mit")
public class StressConfig extends ConfigBase {
    protected final String modid;
    protected final Object2DoubleMap<ResourceLocation> defaultImpacts = new Object2DoubleOpenHashMap<>();
    protected final Object2DoubleMap<ResourceLocation> defaultCapacities = new Object2DoubleOpenHashMap<>();
    protected final Map<ResourceLocation, ConfigValue<Double>> impacts = new HashMap<>();
    protected final Map<ResourceLocation, ConfigValue<Double>> capacities = new HashMap<>();

    public StressConfig(String modid) {
        this.modid = modid;
    }

    protected int getVersion() {
        return 1;
    }

    @Override
    public void registerAll(Builder builder) {
        builder.comment(".", Comments.su, Comments.impact).push("impact");
        defaultImpacts.forEach((id, value) -> this.impacts.put(id, builder.define(id.getPath(), value)));
        builder.pop();

        builder.comment(".", Comments.su, Comments.capacity).push("capacity");
        defaultCapacities.forEach((id, value) -> this.capacities.put(id, builder.define(id.getPath(), value)));
        builder.pop();
    }

    @Override
    public String getName() {
        return "stressValues.v" + getVersion();
    }

    public @Nullable DoubleSupplier getImpact(Block block) {
        ResourceLocation id = RegisteredObjects.getKeyOrThrow(block);
        ConfigValue<Double> value = this.impacts.get(id);
        return value == null ? null : value::get;
    }

    public @Nullable DoubleSupplier getCapacity(Block block) {
        ResourceLocation id = RegisteredObjects.getKeyOrThrow(block);
        ConfigValue<Double> value = this.capacities.get(id);
        return value == null ? null : value::get;
    }

    public <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
        return setImpact(0);
    }

    public <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
        return builder -> {
            ResourceLocation id = new ResourceLocation(modid, builder.getName());
            defaultImpacts.put(id, value);
            return builder;
        };
    }

    public <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
        return builder -> {
            ResourceLocation id = new ResourceLocation(modid, builder.getName());
            defaultCapacities.put(id, value);
            return builder;
        };
    }

    static class Comments {
        static String su = "[in Stress Units]";
        static String impact = "Configure the individual stress impact of mechanical blocks.";
        static String capacity = "Configure how much stress a source can accommodate for.";
    }
}
