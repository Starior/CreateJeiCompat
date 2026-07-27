package com.starion.createjeicompat.jei;

import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Rebuilds JEI's cached recipe layouts in place after a page change.
 */
public final class JeiLayoutRefresher {

    private static Field logicField;
    private static Field cachedLayoutsField;
    private static Method updateLayoutMethod;
    private static boolean disabled;

    private JeiLayoutRefresher() {
    }

    public static void refreshIfShowing() {
        if (disabled) {
            return;
        }
        try {
            if (!(Minecraft.getInstance().screen instanceof RecipesGui recipesGui)) {
                return;
            }
            if (logicField == null) {
                logicField = RecipesGui.class.getDeclaredField("logic");
                logicField.setAccessible(true);
            }
            Object logic = logicField.get(recipesGui);
            if (logic == null) {
                return;
            }
            if (cachedLayoutsField == null) {
                cachedLayoutsField = logic.getClass().getDeclaredField("cachedRecipeLayoutsWithButtons");
                cachedLayoutsField.setAccessible(true);
            }
            cachedLayoutsField.set(logic, null);
            if (updateLayoutMethod == null) {
                updateLayoutMethod = RecipesGui.class.getDeclaredMethod("updateLayout");
                updateLayoutMethod.setAccessible(true);
            }
            updateLayoutMethod.invoke(recipesGui);
        } catch (Throwable t) {
            disabled = true;
        }
    }
}
