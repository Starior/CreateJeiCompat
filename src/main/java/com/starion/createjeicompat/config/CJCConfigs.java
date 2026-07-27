package com.starion.createjeicompat.config;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public final class CJCConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static CClient client;

    private CJCConfigs() {
    }

    public static CClient client() {
        return client;
    }

    /** Safe before config load; defaults to arrows off. */
    public static boolean showPageArrows() {
        return client != null && Boolean.TRUE.equals(client.showPageArrows.get());
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });
        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register(ModContainer container) {
        client = register(CClient::new, ModConfig.Type.CLIENT);
        for (Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) {
            container.registerConfig(pair.getKey(), pair.getValue().specification);
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values()) {
            if (config.specification == event.getConfig().getSpec()) {
                config.onLoad();
            }
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values()) {
            if (config.specification == event.getConfig().getSpec()) {
                config.onReload();
            }
        }
    }
}
