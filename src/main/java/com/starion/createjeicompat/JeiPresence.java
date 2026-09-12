package com.starion.createjeicompat;

/**
 * Distinguishes a real JEI install from a JEI API stand-in such as TMRV.
 *
 * TMRV registers the {@code jei} mod id so {@code ModList.isLoaded("jei")} is true,
 * but it does not ship JEI GUI internals like {@code mezz.jei.gui.recipes.RecipesGui}.
 */
public final class JeiPresence {

    private static final String RECIPES_GUI = "mezz.jei.gui.recipes.RecipesGui";

    private JeiPresence() {
    }

    /**
     * True only when JEI's recipe screen class is on the classpath.
     * Safe during mixin prepare (no {@code ModList} required).
     */
    public static boolean hasRecipesGui() {
        try {
            Class.forName(RECIPES_GUI, false, JeiPresence.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
            return false;
        }
    }
}
