/*
 * Copyright (C) 2025  DragonsPlus
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
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
 */

package plus.dragons.createdragonsplus.mixin.minecraft;

import java.util.HashMap;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createdragonsplus.common.recipe.UpdateRecipesEvent;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Shadow
    @Final
    private RecipeManager recipes;

    @Inject(method = "updateRegistryTags()V", at = @At("TAIL"))
    private void updateRegistryTags$postBeforeRecipeSyncEvent(CallbackInfo ci) {
        var accessor = (RecipeManagerAccessor) this.recipes;
        var byType = new HashMap<>(accessor.getByType());
        for (var entry : byType.entrySet()) {
            byType.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        var byName = new HashMap<>(accessor.getByName());
        MinecraftForge.EVENT_BUS.post(new UpdateRecipesEvent(recipes, byType, byName)).apply();
    }
}
