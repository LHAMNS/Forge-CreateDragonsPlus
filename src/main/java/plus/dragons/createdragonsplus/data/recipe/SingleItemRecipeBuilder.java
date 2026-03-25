/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.data.recipe;

import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.jetbrains.annotations.Nullable;

public class SingleItemRecipeBuilder extends BaseSingleItemRecipeBuilder<SingleItemRecipe, SingleItemRecipeBuilder> {
    private final SingleItemRecipe.Factory<?> factory;
    private String group = "";

    public SingleItemRecipeBuilder(@Nullable String directory, SingleItemRecipe.Factory<?> factory) {
        super(directory);
        this.factory = factory;
    }

    public SingleItemRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    @Override
    protected SingleItemRecipeBuilder builder() {
        return this;
    }

    @Override
    public SingleItemRecipe build() {
        if (id == null) {
            throw new IllegalStateException("Recipe id is not set");
        }
        return (SingleItemRecipe) this.factory.create(id, group, ingredient, result);
    }
}
