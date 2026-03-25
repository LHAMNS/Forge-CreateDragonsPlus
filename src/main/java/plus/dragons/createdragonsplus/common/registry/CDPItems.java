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

package plus.dragons.createdragonsplus.common.registry;

import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;
import java.util.EnumMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.Tags;
import plus.dragons.createdragonsplus.client.texture.CDPGuiTextures;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.fluids.dye.DyeColors;
import plus.dragons.createdragonsplus.data.tag.ItemTagRegistry;

public class CDPItems {
    public static final CommonTags COMMON_TAGS = new CommonTags();
    public static final ModTags MOD_TAGS = new ModTags();

    public static final ItemEntry<SmithingTemplateItem> BLAZE_UPGRADE_SMITHING_TEMPLATE = REGISTRATE
            .item("blaze_upgrade_smithing_template", prop -> new SmithingTemplateItem(
                    Tooltips.BLAZE_UPGRADE_APPLIES_TO,
                    Tooltips.BLAZE_UPGRADE_INGREDIENTS,
                    Tooltips.BLAZE_UPGRADE,
                    Tooltips.BLAZE_UPGRADE_BASE_SLOT,
                    Tooltips.BLAZE_UPGRADE_ADDITIONS_SLOT,
                    CDPGuiTextures.BLAZE_UPGRADE_BASE_SLOT_ICONS,
                    CDPGuiTextures.BLAZE_UPGRADE_ADDITIONS_SLOT_ICONS))
            .lang("Smithing Template")
            .register();

    public static void register(IEventBus modBus) {
        REGISTRATE.registerItemTags(COMMON_TAGS);
        REGISTRATE.registerItemTags(MOD_TAGS);
    }

    public static class Tooltips {
        public static final Component BLAZE_UPGRADE_APPLIES_TO = REGISTRATE.addLang("item",
                new ResourceLocation(CDPCommon.ID, "smithing_template.blaze_upgrade.applies_to"),
                "Blaze Burner").withStyle(ChatFormatting.BLUE);
        public static final Component BLAZE_UPGRADE_INGREDIENTS = REGISTRATE.addLang("item",
                new ResourceLocation(CDPCommon.ID, "smithing_template.blaze_upgrade.ingredients"),
                "Working blocks for Blaze").withStyle(ChatFormatting.BLUE);
        public static final Component BLAZE_UPGRADE = REGISTRATE.addLang("upgrade",
                new ResourceLocation(CDPCommon.ID, "blaze_upgrade"),
                "Blaze Upgrade").withStyle(ChatFormatting.GRAY);
        public static final Component BLAZE_UPGRADE_BASE_SLOT = REGISTRATE.addLang("item",
                new ResourceLocation(CDPCommon.ID, "smithing_template.blaze_upgrade.base_slot_description"),
                "Add Blaze Burner");
        public static final Component BLAZE_UPGRADE_ADDITIONS_SLOT = REGISTRATE.addLang("item",
                new ResourceLocation(CDPCommon.ID, "smithing_template.blaze_upgrade.additions_slot_description"),
                "Add working blocks for Blaze");
    }

    public static class CommonTags extends ItemTagRegistry {
        public final TagKey<Item> dyeBuckets = tag("buckets/dye", "Dye Buckets");
        public final EnumMap<DyeColor, TagKey<Item>> dyeBucketsByColor = Util.make(new EnumMap<>(DyeColor.class), map -> {
            for (var color : DyeColors.ALL) {
                var tag = tag("buckets/dye/" + color.getName(), DyeColors.LOCALIZATION.get(color) + " Dye Buckets");
                map.put(color, tag);
                addTag(this.dyeBuckets, tag);
            }
        });
        public final TagKey<Item> dragonBreathBuckets = tag("buckets/dragon_breath", "Dragon Breath Buckets");

        protected CommonTags() {
            super("c");
            addTag(Tags.Items.BUCKETS, dyeBuckets);
            addTag(Tags.Items.BUCKETS, dragonBreathBuckets);
        }
    }

    public static class ModTags extends ItemTagRegistry {
        public final TagKey<Item> notApplicableColoring = tag("not_applicable_for_coloring", "Not applicable for automatic Coloring Recipe");

        protected ModTags() {
            super(CDPCommon.ID);
        }
    }
}
