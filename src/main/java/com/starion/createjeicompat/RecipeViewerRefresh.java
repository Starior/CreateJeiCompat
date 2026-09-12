package com.starion.createjeicompat;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

/**
 * Rebuilds the open recipe viewer after a page change.
 * JEI/EMI classes are touched only via reflection so either mod can be absent.
 */
public final class RecipeViewerRefresh {

    private RecipeViewerRefresh() {
    }

    public static void schedule() {
        Minecraft.getInstance().execute(RecipeViewerRefresh::refreshNow);
    }

    private static void refreshNow() {
        if (JeiPresence.hasRecipesGui()) {
            invokeStatic("com.starion.createjeicompat.jei.JeiLayoutRefresher", "refreshIfShowing");
        }
        if (ModList.get().isLoaded("emi")) {
            invokeStatic("com.starion.createjeicompat.emi.EmiLayoutRefresher", "refreshIfShowing");
        }
    }

    private static void invokeStatic(String className, String methodName) {
        try {
            Class.forName(className).getMethod(methodName).invoke(null);
        } catch (Throwable ignored) {
            // Viewer internals vary by version; pagination draw/tooltips still update.
        }
    }
}
