package com.starion.createjeicompat.emi;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.starion.createjeicompat.RecipeViewerRefresh;
import com.starion.createjeicompat.SequencedAssemblyPageManager;
import dev.emi.emi.jemi.JemiRecipe;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.WidgetGroup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Wheel over a sequenced-assembly recipe in EMI: blocks EMI recipe-page scroll;
 * multi-page recipes also advance step pages.
 */
public class EmiScrollHandler {

    private Field currentPageField;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof RecipeScreen)) {
            return;
        }

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();
        double scrollDelta = event.getScrollDelta();

        try {
            List<WidgetGroup> groups = getCurrentPageGroups(screen);
            if (groups == null || groups.isEmpty()) {
                return;
            }

            for (WidgetGroup group : groups) {
                if (group == null || group.recipe == null) {
                    continue;
                }
                if (!isOverGroup(group, mouseX, mouseY)) {
                    continue;
                }

                SequencedAssemblyRecipe sequenced = unwrapSequenced(group.recipe);
                if (sequenced == null) {
                    return;
                }

                // Consume scroll over any sequenced recipe so EMI does not flip recipe pages
                // (including single-page ones). Multi-page recipes also advance step pages.
                if (SequencedAssemblyPageManager.handleScroll(sequenced, scrollDelta)) {
                    RecipeViewerRefresh.schedule();
                }
                event.setCanceled(true);
                return;
            }
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    private List<WidgetGroup> getCurrentPageGroups(Screen screen) throws Exception {
        if (currentPageField == null) {
            currentPageField = RecipeScreen.class.getDeclaredField("currentPage");
            currentPageField.setAccessible(true);
        }
        Object value = currentPageField.get(screen);
        if (value instanceof List<?> list) {
            return (List<WidgetGroup>) list;
        }
        return null;
    }

    private static boolean isOverGroup(WidgetGroup group, double mouseX, double mouseY) {
        return mouseX >= group.x && mouseX < group.x + group.width
                && mouseY >= group.y && mouseY < group.y + group.height;
    }

    private static SequencedAssemblyRecipe unwrapSequenced(Object emiRecipe) {
        if (emiRecipe instanceof JemiRecipe<?> jemi && jemi.recipe instanceof SequencedAssemblyRecipe sequenced) {
            return sequenced;
        }
        if (emiRecipe instanceof SequencedAssemblyRecipe sequenced) {
            return sequenced;
        }
        return null;
    }
}
