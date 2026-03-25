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
 */

package plus.dragons.createdragonsplus.config;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class FeaturesConfig extends ConfigBase {
    private static final ConcurrentHashMap<ResourceLocation, ConfigFeature> FEATURES = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, ConfigFeature> FEATURES_VIEW = Collections.unmodifiableMap(FEATURES);
    protected final String modid;

    public FeaturesConfig(String modid) {
        this.modid = modid;
    }

    public static @UnmodifiableView Map<ResourceLocation, ConfigFeature> getFeatures() {
        return FEATURES_VIEW;
    }

    public static boolean isFeatureEnabled(ResourceLocation id) {
        return FEATURES.containsKey(id) && FEATURES.get(id).get();
    }

    protected @Nullable Boolean getFeatureOverride(ResourceLocation id) {
        String key = id.toString();
        Boolean override = null;
        for (var mod : ModList.get().getMods()) {
            var properties = mod.getModProperties();
            if (properties.get(key) instanceof Boolean flag) {
                override = flag;
            }
        }
        return override;
    }

    protected ConfigFeature feature(boolean enabled, String name, String... comment) {
        return new ConfigFeature(name, enabled, comment);
    }

    public class ConfigFeature extends ConfigBool implements ICondition {
        private final ResourceLocation id;
        private final @Nullable Boolean override;

        public ConfigFeature(String name, boolean def, String... comment) {
            super(name, def, comment);
            this.id = new ResourceLocation(modid, name);
            this.override = getFeatureOverride(this.id);
            if (FEATURES.containsKey(id))
                throw new IllegalStateException("Config features with id [" + id + "] already registered");
            FEATURES.put(id, this);
        }

        public ResourceLocation getId() {
            return id;
        }

        public ConfigFeature addAlias(String name) {
            FEATURES.put(new ResourceLocation(modid, name), this);
            return this;
        }

        @Override
        public Boolean get() {
            return override == null ? super.get() : override;
        }

        @Override
        public boolean test(ICondition.IContext context) {
            return get();
        }

        @Override
        public ResourceLocation getID() {
            return ConfigFeatureConditionSerializer.ID;
        }
    }

    public static class ConfigFeatureConditionSerializer implements ICondition.IConditionSerializer<ConfigFeature> {
        public static final ConfigFeatureConditionSerializer INSTANCE = new ConfigFeatureConditionSerializer();
        public static final ResourceLocation ID = new ResourceLocation("create_dragons_plus", "config_feature");

        @Override
        public void write(JsonObject json, ConfigFeature value) {
            json.addProperty("feature", value.getId().toString());
        }

        @Override
        public ConfigFeature read(JsonObject json) {
            ResourceLocation featureId = new ResourceLocation(json.get("feature").getAsString());
            ConfigFeature feature = FEATURES.get(featureId);
            if (feature == null) {
                throw new IllegalStateException("No config feature with id [" + featureId + "] exists");
            }
            return feature;
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }

    @Override
    public String getName() {
        return "features";
    }
}
