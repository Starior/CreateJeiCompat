package com.starion.createjeicompat;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import java.util.WeakHashMap;

/**
 * Manages pagination state for sequenced assembly recipes.
 * This is a separate utility class to avoid Mixin class loading issues.
 */
public class SequencedAssemblyPageManager {
    
    private static final int STEPS_PER_PAGE = 6;
    private static final WeakHashMap<SequencedAssemblyRecipe, Integer> currentPageMap = new WeakHashMap<>();
    
    /**
     * Get current page for a recipe (0-indexed).
     */
    public static int getCurrentPage(SequencedAssemblyRecipe recipe) {
        return currentPageMap.getOrDefault(recipe, 0);
    }
    
    /**
     * Set current page for a recipe.
     */
    public static void setCurrentPage(SequencedAssemblyRecipe recipe, int page) {
        int totalSteps = recipe.getSequence().size();
        int totalPages = calculateTotalPages(totalSteps);
        if (totalPages > 0) {
            currentPageMap.put(recipe, Math.max(0, Math.min(page, totalPages - 1)));
        }
    }
    
    /**
     * Handle scroll for a sequenced assembly recipe.
     * @return true if scroll was handled, false otherwise
     */
    public static boolean handleScroll(SequencedAssemblyRecipe recipe, double scrollDelta) {
        if (recipe == null) {
            return false;
        }
        
        int totalSteps = recipe.getSequence().size();
        int totalPages = calculateTotalPages(totalSteps);
        if (totalPages <= 1) {
            return false;
        }

        int currentPage = getCurrentPage(recipe);
        if (scrollDelta > 0 && currentPage > 0) {
            setCurrentPage(recipe, currentPage - 1);
            return true;
        } else if (scrollDelta < 0 && currentPage < totalPages - 1) {
            setCurrentPage(recipe, currentPage + 1);
            return true;
        }
        return false;
    }
    
    /**
     * Calculate total pages for given number of steps.
     * Extracted to avoid code duplication.
     */
    private static int calculateTotalPages(int totalSteps) {
        return (int) Math.ceil(totalSteps / (double) STEPS_PER_PAGE);
    }
    
    /**
     * Get steps per page constant.
     */
    public static int getStepsPerPage() {
        return STEPS_PER_PAGE;
    }
}

