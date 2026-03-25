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

package plus.dragons.createdragonsplus.common.advancements.criterion;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import plus.dragons.createdragonsplus.common.CDPCommon;
import plus.dragons.createdragonsplus.common.registry.CDPCriterions;
import plus.dragons.createdragonsplus.util.CDPCodecs;

public class StatTrigger implements CriterionTrigger<StatTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(CDPCommon.ID, "stat");
    private final Table<PlayerAdvancements, Stat<?>, Set<Listener<Instance>>> listeners = Tables
            .newCustomTable(new IdentityHashMap<>(), IdentityHashMap::new);

    public StatTrigger() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void checkTriggers(ServerPlayer player, Stat<?> stat, int value) {
        PlayerAdvancements advancements = player.getAdvancements();
        var listenerSet = this.listeners.get(advancements, stat);
        if (listenerSet == null || listenerSet.isEmpty())
            return;
        for (var listener : listenerSet) {
            var trigger = listener.getTriggerInstance();
            if (trigger.bounds.matches(value)) {
                listener.run(advancements);
            }
        }
    }

    @Override
    public final void addPlayerListener(PlayerAdvancements advancements, Listener<Instance> listener) {
        var stat = listener.getTriggerInstance().stat;
        var set = this.listeners.get(advancements, stat);
        if (set == null) {
            set = new HashSet<>();
            this.listeners.put(advancements, stat, set);
        }
        set.add(listener);
    }

    @Override
    public final void removePlayerListener(PlayerAdvancements advancements, Listener<Instance> listener) {
        var stat = listener.getTriggerInstance().stat;
        var set = this.listeners.get(advancements, stat);
        if (set != null) {
            set.remove(listener);
            if (set.isEmpty())
                this.listeners.remove(advancements, stat);
        }
    }

    @Override
    public final void removePlayerListeners(PlayerAdvancements advancements) {
        this.listeners.rowMap().remove(advancements);
    }

    @Override
    public Instance createInstance(JsonObject json, DeserializationContext context) {
        Stat<?> stat = CDPCodecs.STAT.parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, s -> { throw new JsonSyntaxException("Failed to parse stat: " + s); });
        MinMaxBounds.Ints bounds = MinMaxBounds.Ints.fromJson(json.get("bounds"));
        return new Instance(stat, bounds);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        final Stat<?> stat;
        final MinMaxBounds.Ints bounds;

        public Instance(Stat<?> stat, MinMaxBounds.Ints bounds) {
            super(ID, EntityPredicate.Composite.ANY);
            this.stat = stat;
            this.bounds = bounds;
        }

        public static Instance of(Stat<?> stat, MinMaxBounds.Ints bounds) {
            return new Instance(stat, bounds);
        }

        public static Instance of(ResourceLocation stat, MinMaxBounds.Ints bounds) {
            return new Instance(Stats.CUSTOM.get(stat), bounds);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            CDPCodecs.STAT.encodeStart(JsonOps.INSTANCE, stat)
                    .resultOrPartial(s -> {})
                    .ifPresent(element -> {
                        if (element.isJsonObject()) {
                            element.getAsJsonObject().entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
                        }
                    });
            json.add("bounds", bounds.serializeToJson());
            return json;
        }
    }
}
