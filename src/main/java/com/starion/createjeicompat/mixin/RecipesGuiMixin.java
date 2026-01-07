package com.starion.createjeicompat.mixin;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.starion.createjeicompat.SequencedAssemblyPageManager;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipesGui.class)
public abstract class RecipesGuiMixin {
    
    @Shadow(remap = false)
    private RecipeGuiLayouts layouts;
    
    @Shadow(remap = false)
    private IRecipeGuiLogic logic;
    
    // Cache reflection objects to avoid repeated lookups (performance optimization)
    private static java.lang.reflect.Field cachedLayoutsField;
    private static java.lang.reflect.Method cachedUpdateLayoutMethod;
    private static final Object CACHE_LOCK = new Object();

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true, remap = false)
    private void createjeicompat$onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (layouts == null) {
            return;
        }
        
        // Only handle if mouse is over RecipesGui
        RecipesGui self = (RecipesGui) (Object) this;
        if (!self.isMouseOver(mouseX, mouseY)) {
            return;
        }
        
        try {
            // Get recipe layouts using reflection (cache the field for performance)
            java.lang.reflect.Field field = cachedLayoutsField;
            if (field == null) {
                synchronized (CACHE_LOCK) {
                    if (cachedLayoutsField == null) {
                        cachedLayoutsField = RecipeGuiLayouts.class.getDeclaredField("recipeLayoutsWithButtons");
                        cachedLayoutsField.setAccessible(true);
                    }
                    field = cachedLayoutsField;
                }
            }
            
            @SuppressWarnings("unchecked")
            List<IRecipeLayoutWithButtons<?>> recipeLayouts = (List<IRecipeLayoutWithButtons<?>>) field.get(layouts);
            
            if (recipeLayouts == null || recipeLayouts.isEmpty()) {
                return;
            }
            
            // Check each recipe layout to see if mouse is over it and if it's a sequenced assembly recipe
            for (IRecipeLayoutWithButtons<?> layoutWithButtons : recipeLayouts) {
                IRecipeLayoutDrawable<?> recipeLayout = layoutWithButtons.getRecipeLayout();
                
                // Check if mouse is over this layout
                if (!recipeLayout.isMouseOver(mouseX, mouseY)) {
                    continue;
                }
                
                // Get recipe from layout - need to check if it's wrapped in RecipeHolder
                Object recipeObj = recipeLayout.getRecipe();
                SequencedAssemblyRecipe sequencedRecipe = null;
                
                // Check if recipe is wrapped in RecipeHolder
                if (recipeObj != null) {
                    try {
                        java.lang.reflect.Method valueMethod = recipeObj.getClass().getMethod("value");
                        Object unwrapped = valueMethod.invoke(recipeObj);
                        if (unwrapped instanceof SequencedAssemblyRecipe) {
                            sequencedRecipe = (SequencedAssemblyRecipe) unwrapped;
                        }
                    } catch (Exception e) {
                        // Not wrapped, check directly
                        if (recipeObj instanceof SequencedAssemblyRecipe) {
                            sequencedRecipe = (SequencedAssemblyRecipe) recipeObj;
                        }
                    }
                }
                
                if (sequencedRecipe != null) {
                    // Handle scroll for sequenced assembly recipe
                    if (SequencedAssemblyPageManager.handleScroll(sequencedRecipe, scrollY)) {
                        // Invalidate the layout cache in RecipeGuiLogic to force recreation
                        // This will cause updateLayout() to rebuild all layouts, calling setRecipe() again
                        try {
                            // Get the actual RecipeGuiLogic implementation (not the interface)
                            Object logicImpl = logic;
                            if (logicImpl != null) {
                                // Invalidate cachedRecipeLayoutsWithButtons to force recreation
                                java.lang.reflect.Field cacheField = logicImpl.getClass().getDeclaredField("cachedRecipeLayoutsWithButtons");
                                cacheField.setAccessible(true);
                                cacheField.set(logicImpl, null);
                            }
                            
                            // Cache updateLayout method for performance
                            java.lang.reflect.Method updateMethod = cachedUpdateLayoutMethod;
                            if (updateMethod == null) {
                                synchronized (CACHE_LOCK) {
                                    if (cachedUpdateLayoutMethod == null) {
                                        cachedUpdateLayoutMethod = RecipesGui.class.getDeclaredMethod("updateLayout");
                                        cachedUpdateLayoutMethod.setAccessible(true);
                                    }
                                    updateMethod = cachedUpdateLayoutMethod;
                                }
                            }
                            updateMethod.invoke(self);
                        } catch (Exception e) {
                            // Silently fail - reflection might fail
                        }
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail - reflection might fail or field might not exist
        }
    }
}

