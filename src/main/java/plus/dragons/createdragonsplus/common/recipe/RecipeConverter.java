/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 * Ported from NeoForge 1.21.1 to Forge 1.20.1
 */

package plus.dragons.createdragonsplus.common.recipe;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.AddReloadListenerEvent;

@EventBusSubscriber
public interface RecipeConverter<K extends Recipe<?>, V extends Recipe<?>> extends Function<K, V> {
    Map<RecipeConverter<?, ?>, Runnable> CACHE_INVALIDATORS = new IdentityHashMap<>();

    @SubscribeEvent
    static void onAddReloadListener(final AddReloadListenerEvent event) {
        event.addListener((ResourceManagerReloadListener) resourceManager -> CACHE_INVALIDATORS.values().forEach(Runnable::run));
    }

    static <K extends Recipe<?>, V extends Recipe<?>> RecipeConverter<K, V> cached(CacheBuilder<Object, Object> cacheBuilder, RecipeConverter<K, V> converter) {
        var cache = cacheBuilder.build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) {
                return converter.apply(key);
            }
        });
        RecipeConverter<K, V> result = cache::getUnchecked;
        CACHE_INVALIDATORS.put(result, cache::invalidateAll);
        return result;
    }
}
