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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CreateJeiCompatMod.MOD_ID)
public class CreateJeiCompatMod {
    public static final String MOD_ID = "createjeicompat";

    public CreateJeiCompatMod(IEventBus modEventBus, ModContainer container) {
        CJCConfigs.register(container);
        modEventBus.register(CJCConfigs.class);

        // TMRV provides the jei mod id without JEI's RecipesGui. Do not load JeiScrollHandler then.
        if (JeiPresence.hasRecipesGui()) {
            NeoForge.EVENT_BUS.register(new JeiScrollHandler());
        }
        if (ModList.get().isLoaded("emi")) {
            NeoForge.EVENT_BUS.register(new EmiScrollHandler());
        }
    }
}
