/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.mixin.garnished;

import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.dakotapride.garnished.registry.JEI.DyeBlowingFanCategory;
import org.spongepowered.asm.mixin.Mixin;
import plus.dragons.createdragonsplus.config.CDPConfig;
import plus.dragons.createdragonsplus.integration.ModIntegration.Constants;

@Restriction(require = @Condition(Constants.CREATE_GARNISHED))
@Mixin(DyeBlowingFanCategory.class)
public abstract class DyeBlowingFanCategoryMixin<T extends StandardProcessingRecipe<?>> extends ProcessingViaFanCategory.MultiOutput<T> {
    private DyeBlowingFanCategoryMixin(Info<T> info) {
        super(info);
    }

    @Override
    public boolean isHandled(T recipe) {
        return !CDPConfig.recipes().enableBulkColoring.get();
    }
}
