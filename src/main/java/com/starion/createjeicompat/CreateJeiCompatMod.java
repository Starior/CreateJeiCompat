package com.starion.createjeicompat;

/**
 * Pagination for Create sequenced-assembly recipes in JEI and EMI (via JEmi).
 *
 * Create: https://github.com/Creators-of-Create/Create
 * JEI: https://github.com/mezz/JustEnoughItems
 * EMI: https://github.com/emilyploszaj/emi
 */
import com.starion.createjeicompat.config.CJCConfigs;
import com.starion.createjeicompat.emi.EmiScrollHandler;
import com.starion.createjeicompat.jei.JeiScrollHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateJeiCompatMod.MOD_ID)
public class CreateJeiCompatMod {
    public static final String MOD_ID = "createjeicompat";

    public CreateJeiCompatMod() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        CJCConfigs.register(modLoadingContext);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(CJCConfigs.class);

        if (ModList.get().isLoaded("jei")) {
            MinecraftForge.EVENT_BUS.register(new JeiScrollHandler());
        }
        if (ModList.get().isLoaded("emi")) {
            MinecraftForge.EVENT_BUS.register(new EmiScrollHandler());
        }
    }
}
