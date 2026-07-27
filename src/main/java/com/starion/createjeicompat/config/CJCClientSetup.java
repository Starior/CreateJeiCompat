package com.starion.createjeicompat.config;

import com.starion.createjeicompat.CreateJeiCompatMod;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Hooks Create JEI Compat into Create/Ponder's {@link BaseConfigScreen}
 * (Create menu → Configure → other mods list, and the mod's Config button).
 */
@Mod.EventBusSubscriber(modid = CreateJeiCompatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CJCClientSetup {

    private CJCClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BaseConfigScreen.setDefaultActionFor(CreateJeiCompatMod.MOD_ID, base -> base
                    .withButtonLabels("Client Settings", null, null)
                    .withSpecs(CJCConfigs.client().specification, null, null));

            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                            (mc, parent) -> new BaseConfigScreen(parent, CreateJeiCompatMod.MOD_ID)));
        });
    }
}
