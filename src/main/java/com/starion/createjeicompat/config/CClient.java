package com.starion.createjeicompat.config;

import net.createmod.catnip.config.ConfigBase;

public class CClient extends ConfigBase {

    public final ConfigGroup client = group(0, "client", Comments.client);

    public final ConfigBool showPageArrows = b(false, "showPageArrows", Comments.showPageArrows);

    @Override
    public String getName() {
        return "client";
    }

    private static class Comments {
        static final String client = "Client-only settings for Create JEI Compat.";
        static final String showPageArrows =
                "Show clickable < > arrows next to the page number on multi-page sequenced assembly recipes. "
                        + "The 1/N label always shows. Mouse wheel and arrow keys still change pages.";
    }
}
