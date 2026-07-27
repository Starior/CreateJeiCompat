package com.starion.createjeicompat.jei;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.starion.createjeicompat.SequencedAssemblyPageManager;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * JEI-only scroll fallback when RecipesGuiMixin does not apply.
 * Registered only if JEI is loaded.
 */
public class JeiScrollHandler {

    private Field layoutsField;
    private Field logicField;
    private Field recipeLayoutsField;
    private Field logicCacheField;
    private Method updateLayoutMethod;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof RecipesGui recipesGui)) {
            return;
        }

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();
        double scrollDelta = event.getScrollDeltaY();

        if (!recipesGui.isMouseOver(mouseX, mouseY)) {
            return;
        }

        try {
            Object layouts = getLayouts(recipesGui);
            if (layouts == null) {
                return;
            }

            @SuppressWarnings("unchecked")
            List<IRecipeLayoutWithButtons<?>> recipeLayouts = (List<IRecipeLayoutWithButtons<?>>) getRecipeLayouts(layouts);
            if (recipeLayouts == null || recipeLayouts.isEmpty()) {
                return;
            }

            for (IRecipeLayoutWithButtons<?> layoutWithButtons : recipeLayouts) {
                IRecipeLayoutDrawable<?> recipeLayout = layoutWithButtons.getRecipeLayout();
                if (!recipeLayout.isMouseOver(mouseX, mouseY)) {
                    continue;
                }

                SequencedAssemblyRecipe sequencedRecipe = unwrapRecipe(recipeLayout.getRecipe());
                if (sequencedRecipe == null) {
                    continue;
                }

                if (!SequencedAssemblyPageManager.canScroll(sequencedRecipe, scrollDelta)) {
                    event.setCanceled(true);
                    return;
                }

                if (SequencedAssemblyPageManager.handleScroll(sequencedRecipe, scrollDelta)) {
                    invalidateAndUpdate(recipesGui);
                    event.setCanceled(true);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private Object getLayouts(RecipesGui gui) throws Exception {
        if (layoutsField == null) {
            layoutsField = RecipesGui.class.getDeclaredField("layouts");
            layoutsField.setAccessible(true);
        }
        return layoutsField.get(gui);
    }

    private Object getRecipeLayouts(Object layouts) throws Exception {
        if (recipeLayoutsField == null) {
            recipeLayoutsField = RecipeGuiLayouts.class.getDeclaredField("recipeLayoutsWithButtons");
            recipeLayoutsField.setAccessible(true);
        }
        return recipeLayoutsField.get(layouts);
    }

    private static SequencedAssemblyRecipe unwrapRecipe(Object recipeObj) {
        if (recipeObj == null) {
            return null;
        }
        try {
            Method valueMethod = recipeObj.getClass().getMethod("value");
            Object unwrapped = valueMethod.invoke(recipeObj);
            if (unwrapped instanceof SequencedAssemblyRecipe r) {
                return r;
            }
        } catch (Exception ignored) {
        }
        if (recipeObj instanceof SequencedAssemblyRecipe r) {
            return r;
        }
        return null;
    }

    private void invalidateAndUpdate(RecipesGui gui) {
        try {
            if (logicField == null) {
                logicField = RecipesGui.class.getDeclaredField("logic");
                logicField.setAccessible(true);
            }
            Object logic = logicField.get(gui);
            if (logic != null) {
                if (logicCacheField == null) {
                    logicCacheField = logic.getClass().getDeclaredField("cachedRecipeLayoutsWithButtons");
                    logicCacheField.setAccessible(true);
                }
                logicCacheField.set(logic, null);
            }

            if (updateLayoutMethod == null) {
                updateLayoutMethod = RecipesGui.class.getDeclaredMethod("updateLayout");
                updateLayoutMethod.setAccessible(true);
            }
            updateLayoutMethod.invoke(gui);
        } catch (Exception ignored) {
        }
    }
}
