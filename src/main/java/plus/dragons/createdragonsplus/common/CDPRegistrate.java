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

package plus.dragons.createdragonsplus.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider.IntrinsicImpl;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fluids.BaseFlowingFluid;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.registrate.builder.CustomStatBuilder;
import plus.dragons.createdragonsplus.data.lang.ForeignLanguageProvider;
import plus.dragons.createdragonsplus.data.tag.IntrinsicTagRegistry;
import plus.dragons.createdragonsplus.mixin.forge.ExistingFileHelperAccessor;
import plus.dragons.createdragonsplus.data.tag.ItemTagRegistry;
import plus.dragons.createdragonsplus.data.tag.TagRegistry;
import plus.dragons.createdragonsplus.util.CodeReference;

@CodeReference(value = CreateRegistrate.class, source = "create", license = "mit")
public class CDPRegistrate extends CreateRegistrate {
    protected @Nullable Function<Item, TooltipModifier> tooltipModifier;
    protected @Nullable ExistingFileHelper existingFileHelper;
    protected @Nullable String templateLocale;

    protected CDPRegistrate(String modid) {
        super(modid);
    }

    public static CDPRegistrate create(String modid) {
        return new CDPRegistrate(modid);
    }

    public final ResourceLocation asResource(String path) {
        return new ResourceLocation(getModid(), path);
    }

    public CDPRegistrate setTooltipModifier(@Nullable Function<Item, TooltipModifier> tooltipModifier) {
        this.tooltipModifier = tooltipModifier;
        return this;
    }

    /* Datagen */

    public <T, P extends RegistrateTagsProvider<T>> CDPRegistrate registerTags(ProviderType<P> type, TagRegistry<T, P> registry) {
        this.addDataGenerator(type, registry::generate);
        return this;
    }

    public CDPRegistrate registerBlockTags(IntrinsicTagRegistry<Block, IntrinsicImpl<Block>> registry) {
        this.addDataGenerator(ProviderType.BLOCK_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerItemTags(ItemTagRegistry registry) {
        this.addDataGenerator(ProviderType.ITEM_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerFluidTags(IntrinsicTagRegistry<Fluid, IntrinsicImpl<Fluid>> registry) {
        this.addDataGenerator(ProviderType.FLUID_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerEntityTags(IntrinsicTagRegistry<EntityType<?>, IntrinsicImpl<EntityType<?>>> registry) {
        this.addDataGenerator(ProviderType.ENTITY_TAGS, registry::generate);
        this.addDataGenerator(ProviderType.LANG, registry::generate);
        return this;
    }

    public CDPRegistrate registerForeignLocalization(String templateLocale) {
        this.templateLocale = templateLocale;
        return this;
    }

    public CDPRegistrate registerForeignLocalization() {
        return this.registerForeignLocalization("en_us");
    }

    public CDPRegistrate registerBuiltinLocalization(String name) {
        this.addDataGenerator(ProviderType.LANG, provider -> this.generateBuiltinLocalization(name, provider));
        return this;
    }

    public CDPRegistrate registerExtraLocalization(Consumer<BiConsumer<String, String>> provideLang) {
        this.addDataGenerator(ProviderType.LANG,
                provider -> {
                    BiConsumer<String, String> langConsumer = provider::add;
                    provideLang.accept(langConsumer);
                });
        return this;
    }

    protected void generateBuiltinLocalization(String name, RegistrateLangProvider provider) {
        ResourceLocation location = asResource("lang/builtin/" + name + ".json");
        Resource resource = this.getExistingResource(PackType.CLIENT_RESOURCES)
                .getResource(location)
                .orElseThrow(() -> new RuntimeException("Failed to find builtin localization: " + location));
        JsonObject json = getJsonFromResource(resource);
        for (Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            provider.add(key, value);
        }
    }

    protected ResourceManager getExistingResource(PackType type) {
        if (this.existingFileHelper == null) {
            throw new IllegalStateException("Can not get existing resource outside datagen");
        }
        ExistingFileHelperAccessor accessor = (ExistingFileHelperAccessor) this.existingFileHelper;
        return switch (type) {
            case CLIENT_RESOURCES -> accessor.getClientResources();
            case SERVER_DATA -> accessor.getServerResources();
        };
    }

    protected static JsonObject getJsonFromResource(Resource resource) {
        try (InputStream inputStream = resource.open()) {
            return GsonHelper.parse(new InputStreamReader(inputStream));
        } catch (IOException exception) {
            throw new JsonIOException(exception);
        }
    }

    @Override
    protected void onData(GatherDataEvent event) {
        super.onData(event);
        boolean client = event.includeClient();
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        this.existingFileHelper = event.getExistingFileHelper();
        if (this.templateLocale != null)
            generator.addProvider(client, new ForeignLanguageProvider(getModid(), this.templateLocale, output, this.existingFileHelper));
    }

    /* Builders */

    public FluidType defaultFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return new FluidType(properties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return stillTexture;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return flowingTexture;
                    }
                });
            }
        };
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, CDPRegistrate> virtualFluid(
            String name,
            FluidBuilder.FluidTypeFactory type,
            NonNullFunction<BaseFlowingFluid.Properties, T> source,
            NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                asResource("fluid/" + name + "_still"),
                asResource("fluid/" + name + "_flow"),
                type, source, flowingFactory));
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, CDPRegistrate> virtualFluid(
            String name,
            ResourceLocation stillTexture,
            ResourceLocation flowTexture,
            FluidBuilder.FluidTypeFactory typeFactory,
            NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory,
            NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                stillTexture, flowTexture, typeFactory, sourceFactory, flowingFactory));
    }

    public FluidBuilder<VirtualFluid, CDPRegistrate> virtualFluid(String name) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                asResource("fluid/" + name + "_still"),
                asResource("fluid/" + name + "_flow"),
                this::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    public FluidBuilder<VirtualFluid, CDPRegistrate> virtualFluid(String name, ResourceLocation stillTexture, ResourceLocation flowTexture) {
        return entry(name, callback -> new VirtualFluidBuilder<>(self(), self(), name, callback,
                stillTexture, flowTexture,
                this::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    public CustomStatBuilder<CDPRegistrate> customStat(String name, Supplier<ResourceLocation> supplier) {
        return this.entry(name, callback -> new CustomStatBuilder<>(this, this, name, callback, supplier));
    }
}
