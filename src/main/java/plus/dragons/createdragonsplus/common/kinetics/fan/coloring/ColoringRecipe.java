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

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import plus.dragons.createdragonsplus.common.registry.CDPRecipes;

public class ColoringRecipe extends ProcessingRecipe<ColoringRecipeInput> {
    private DyeColor color;

    public ColoringRecipe(ProcessingRecipeParams params) {
        super(CDPRecipes.COLORING, params);
    }

    public static Builder builder(ResourceLocation id, DyeColor color) {
        return new Builder(id, color);
    }

    public static class Builder extends ProcessingRecipeBuilder<ColoringRecipe> {
        private final DyeColor color;

        public Builder(ResourceLocation id, DyeColor color) {
            super(ColoringRecipe::new, id);
            this.color = color;
        }

        @Override
        public ColoringRecipe build() {
            ColoringRecipe recipe = super.build();
            recipe.color = this.color;
            return recipe;
        }
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    public boolean matches(ColoringRecipeInput input, Level level) {
        return color == input.color() && this.ingredients.get(0).test(input.item());
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        buffer.writeEnum(color);
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        color = buffer.readEnum(DyeColor.class);
    }

    @Override
    public void writeExtra(JsonObject json) {
        super.writeExtra(json);
        json.addProperty("color", color.getSerializedName());
    }

    @Override
    public void readExtra(JsonObject json) {
        super.readExtra(json);
        color = DyeColor.byName(json.get("color").getAsString(), DyeColor.WHITE);
    }
}
