package com.starion.createjeicompat;

/**
 * Create JEI Compat - A compatibility mod that adds pagination support
 * for Create mod's sequenced assembly recipes in JEI.
 * 
 * This mod extends Create mod (https://github.com/Creators-of-Create/Create)
 * and JEI (https://github.com/mezz/JustEnoughItems) functionality by enhancing
 * the JEI display of sequenced assembly recipes with pagination, allowing
 * recipes with more than 6 steps to be properly displayed.
 */
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CreateJeiCompatMod.MOD_ID)
public class CreateJeiCompatMod {
    public static final String MOD_ID = "createjeicompat";

    public CreateJeiCompatMod(IEventBus modEventBus) {
        // Register anything here
    }
}

