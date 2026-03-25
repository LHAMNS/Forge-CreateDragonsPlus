/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.integration;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class CompatUtility {
    public static Optional<Item> INDUSTRIAL_FAN;

    public static List<Supplier<? extends ItemStack>> catalystWithIndustryFan(ItemStack fan) {
        if (INDUSTRIAL_FAN == null) {
            var item = ForgeRegistries.ITEMS.getValue(ModIntegration.CREATE_DND.asResource("industrial_fan"));
            INDUSTRIAL_FAN = item != null && item != net.minecraft.world.item.Items.AIR ? Optional.of(item) : Optional.empty();
        }
        return INDUSTRIAL_FAN.<List<Supplier<? extends ItemStack>>>map(item -> List.of(() -> fan, () -> new ItemStack(item))).orElseGet(() -> List.of(() -> fan));
    }
}
