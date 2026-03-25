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

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

public class BuiltinTrigger implements CriterionTrigger<BuiltinTrigger.Instance> {
    private final Map<PlayerAdvancements, Set<Listener<Instance>>> listeners = new IdentityHashMap<>();
    private final ResourceLocation id;

    public BuiltinTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public Instance instance() {
        return new Instance(id);
    }

    public void trigger(ServerPlayer player) {
        var advancements = player.getAdvancements();
        if (this.listeners.containsKey(advancements)) {
            this.listeners.get(advancements).forEach(listener -> listener.run(advancements));
        }
    }

    @Override
    public final void addPlayerListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        this.listeners.computeIfAbsent(playerAdvancements, it -> Sets.newHashSet()).add(listener);
    }

    @Override
    public final void removePlayerListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        Set<Listener<Instance>> set = this.listeners.get(playerAdvancements);
        if (set != null) {
            set.remove(listener);
            if (set.isEmpty()) {
                this.listeners.remove(playerAdvancements);
            }
        }
    }

    @Override
    public final void removePlayerListeners(PlayerAdvancements playerAdvancements) {
        this.listeners.remove(playerAdvancements);
    }

    @Override
    public Instance createInstance(JsonObject json, DeserializationContext context) {
        return new Instance(id);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ResourceLocation criterion) {
            super(criterion, EntityPredicate.Composite.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            return new JsonObject();
        }
    }
}
