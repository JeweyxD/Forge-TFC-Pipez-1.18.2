package com.jewey.tfc_pipez.event;

import com.jewey.tfc_pipez.TFC_Pipez;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.PathResourcePack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;


public class ModEvents {

    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ModEvents::onPackFinder);
    }

    /*
    MIT License
    Copyright (c) 2022 eerussianguy
     */
    public static void onPackFinder(AddPackFindersEvent event)
    {
        try
        {
            if (event.getPackType() == PackType.CLIENT_RESOURCES)
            {
                var modFile = ModList.get().getModFileById(TFC_Pipez.MOD_ID).getFile();
                var resourcePath = modFile.getFilePath();
                var pack = new PathResourcePack(modFile.getFileName() + ":overload", resourcePath)
                {
                    @Nonnull
                    @Override
                    protected Path resolve(@Nonnull String... paths)
                    {
                        return modFile.findResource(paths);
                    }
                };
                var metadata = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                if (metadata != null)
                {
                    TFC_Pipez.LOGGER.info("Injecting tfc_pipez override pack");
                    event.addRepositorySource((consumer, constructor) ->
                            consumer.accept(constructor.create("builtin/tfc_pipez_data", new TextComponent("TFC-Pipez Resources"), true, () -> pack, metadata, Pack.Position.TOP, PackSource.BUILT_IN, false))
                    );
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
