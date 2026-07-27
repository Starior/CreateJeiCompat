package com.starion.createjeicompat;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import java.util.WeakHashMap;

/**
 * Pagination state for sequenced assembly recipes shown in JEI/EMI.
 */
public class SequencedAssemblyPageManager {

    private static final int STEPS_PER_PAGE = 6;
    private static final WeakHashMap<SequencedAssemblyRecipe, Integer> currentPageMap = new WeakHashMap<>();

    public static int getStepsPerPage() {
        return STEPS_PER_PAGE;
    }

    public static int getTotalPages(SequencedAssemblyRecipe recipe) {
        return Math.max(1, (int) Math.ceil(recipe.getSequence().size() / (double) STEPS_PER_PAGE));
    }

    public static int getCurrentPage(SequencedAssemblyRecipe recipe) {
        int page = currentPageMap.getOrDefault(recipe, 0);
        return Math.max(0, Math.min(page, getTotalPages(recipe) - 1));
    }

    public static void setCurrentPage(SequencedAssemblyRecipe recipe, int page) {
        currentPageMap.put(recipe, Math.max(0, Math.min(page, getTotalPages(recipe) - 1)));
    }

    public static boolean previousPage(SequencedAssemblyRecipe recipe) {
        int current = getCurrentPage(recipe);
        if (current <= 0) {
            return false;
        }
        setCurrentPage(recipe, current - 1);
        return true;
    }

    public static boolean nextPage(SequencedAssemblyRecipe recipe) {
        int current = getCurrentPage(recipe);
        if (current >= getTotalPages(recipe) - 1) {
            return false;
        }
        setCurrentPage(recipe, current + 1);
        return true;
    }

    public static boolean canScroll(SequencedAssemblyRecipe recipe, double scrollDelta) {
        if (recipe == null || getTotalPages(recipe) <= 1) {
            return false;
        }
        int currentPage = getCurrentPage(recipe);
        if (scrollDelta > 0) {
            return currentPage > 0;
        }
        if (scrollDelta < 0) {
            return currentPage < getTotalPages(recipe) - 1;
        }
        return false;
    }

    public static boolean handleScroll(SequencedAssemblyRecipe recipe, double scrollDelta) {
        if (recipe == null) {
            return false;
        }
        if (scrollDelta > 0) {
            return previousPage(recipe);
        }
        if (scrollDelta < 0) {
            return nextPage(recipe);
        }
        return false;
    }
}
