package com.starion.createjeicompat.emi;

import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

/**
 * Rebuilds EMI's RecipeScreen widgets in place after a page change.
 */
public final class EmiLayoutRefresher {

    private static Field tabPageField;
    private static Field tabField;
    private static Field pageField;
    private static boolean disabled;

    private EmiLayoutRefresher() {
    }

    public static void refreshIfShowing() {
        if (disabled) {
            return;
        }
        try {
            if (!(Minecraft.getInstance().screen instanceof RecipeScreen recipeScreen)) {
                return;
            }
            if (tabPageField == null) {
                tabPageField = RecipeScreen.class.getDeclaredField("tabPage");
                tabPageField.setAccessible(true);
                tabField = RecipeScreen.class.getDeclaredField("tab");
                tabField.setAccessible(true);
                pageField = RecipeScreen.class.getDeclaredField("page");
                pageField.setAccessible(true);
            }
            int tabPage = tabPageField.getInt(recipeScreen);
            int tab = tabField.getInt(recipeScreen);
            int page = pageField.getInt(recipeScreen);
            recipeScreen.setPage(tabPage, tab, page);
        } catch (Throwable t) {
            disabled = true;
        }
    }
}
