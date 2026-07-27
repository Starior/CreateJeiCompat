package com.starion.createjeicompat.config;

import com.starion.createjeicompat.CreateJeiCompatMod;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Hooks Create JEI Compat into Create/Ponder's {@link BaseConfigScreen}
 * (Create menu → Configure → other mods list, and the mod's Config button).
 */
@EventBusSubscriber(modid = CreateJeiCompatMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CJCClientSetup {

    private CJCClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BaseConfigScreen.setDefaultActionFor(CreateJeiCompatMod.MOD_ID, base -> base
                    .withButtonLabels("Client Settings", null, null)
                    .withSpecs(CJCConfigs.client().specification, null, null));

            ModContainer container = ModList.get().getModContainerById(CreateJeiCompatMod.MOD_ID).orElse(null);
            if (container != null) {
                container.registerExtensionPoint(
                        IConfigScreenFactory.class,
                        (mc, parent) -> new BaseConfigScreen(parent, CreateJeiCompatMod.MOD_ID));
            }
        });
    }
}
