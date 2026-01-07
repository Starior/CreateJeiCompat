package com.starion.createjeicompat.mixin;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.SequencedAssemblyCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.starion.createjeicompat.SequencedAssemblyPageManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mixin(value = SequencedAssemblyCategory.class)
public abstract class SequencedAssemblyCategoryMixin {

    @Unique
    private static final String[] EXTENDED_ROMANS = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII"};

    @Unique
    private static final int STEP_MARGIN = 3;

    @Shadow(remap = false)
    Map<ResourceLocation, SequencedAssemblySubCategory> subCategories;

    @Invoker(value = "getSubCategory", remap = false)
    abstract SequencedAssemblySubCategory invokeGetSubCategory(SequencedRecipe<?> sequencedRecipe);

    @Invoker(value = "chanceComponent", remap = false)
    abstract MutableComponent invokeChanceComponent(float chance);

    /**
     * Helper method to get background. Since getBackground() is in parent class,
     * we cast to the parent type to access it.
     * Cached to avoid repeated casts and method calls.
     */
    @Unique
    private IDrawable cachedBackground;
    
    @Unique
    private IDrawable getBackgroundHelper() {
        if (cachedBackground == null) {
            cachedBackground = ((com.simibubi.create.compat.jei.category.CreateRecipeCategory<?>) (Object) this).getBackground();
        }
        return cachedBackground;
    }


    /**
     * Overwrite setRecipe to support pagination (6 steps per page).
     */
    @Overwrite(remap = false)
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses) {
        boolean noRandomOutput = recipe.getOutputChance() == 1;
        int xOffset = noRandomOutput ? 0 : -7;

        builder
                .addSlot(RecipeIngredientRole.INPUT, 27 + xOffset, 91)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addItemStacks(List.of(recipe.getIngredient().getItems()));
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 132 + xOffset, 91)
                .setBackground(CreateRecipeCategory.getRenderedSlot(recipe.getOutputChance()), -1 , -1)
                .addItemStack(CreateRecipeCategory.getResultItem(recipe))
                .addTooltipCallback((recipeSlotView, tooltip) -> {
                    if (noRandomOutput)
                        return;

                    float chance = recipe.getOutputChance();
                    tooltip.add(1, invokeChanceComponent(chance));
                });

        List<SequencedRecipe<?>> sequence = recipe.getSequence();
        int totalSteps = sequence.size();
        int currentPage = SequencedAssemblyPageManager.getCurrentPage(recipe);
        int stepsPerPage = SequencedAssemblyPageManager.getStepsPerPage();
        int startIndex = currentPage * stepsPerPage;
        int endIndex = Math.min(startIndex + stepsPerPage, totalSteps);

        // Calculate width of current page for centering
        int pageWidth = 0;
        for (int i = startIndex; i < endIndex; i++) {
            SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequence.get(i));
            pageWidth += subCategory.getWidth() + STEP_MARGIN;
        }
        if (pageWidth > 0) {
            pageWidth -= STEP_MARGIN; // Remove last margin
        }

        int bgWidth = getBackgroundHelper().getWidth();
        int x = bgWidth / 2 - pageWidth / 2;

        // Position steps for current page
        for (int i = startIndex; i < endIndex; i++) {
            SequencedRecipe<?> sequencedRecipe = sequence.get(i);
            SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequencedRecipe);
            subCategory.setRecipe(builder, sequencedRecipe, focuses, x);
            x += subCategory.getWidth() + STEP_MARGIN;
        }

        // Handle loops - add invisible ingredients for all steps in all loops
        for (int i = 1; i < recipe.getLoops(); i++) {
            for (SequencedRecipe<?> sequencedRecipe : sequence) {
                NonNullList<Ingredient> sequencedIngredients = sequencedRecipe.getRecipe().getIngredients();
                for (Ingredient ingredient : sequencedIngredients.subList(1, sequencedIngredients.size()))
                    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(ingredient);
                for (SizedFluidIngredient fluidIngredient : sequencedRecipe.getRecipe().getFluidIngredients())
                    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.asList(fluidIngredient.getFluids()));
            }
        }
    }

    /**
     * Overwrite draw to support pagination.
     */
    @Overwrite(remap = false)
    public void draw(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        graphics.pose().pushPose();

        graphics.pose().pushPose();
        graphics.pose().translate(0, 15, 0);
        boolean singleOutput = recipe.getOutputChance() == 1;
        int xOffset = singleOutput ? 0 : -7;
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52 + xOffset, 79);
        if (!singleOutput) {
            AllGuiTextures.JEI_CHANCE_SLOT.render(graphics, 150 + xOffset, 75);
            Component component = Component.literal("?").withStyle(ChatFormatting.BOLD);
            graphics.drawString(font, component, font.width(component) / -2 + 8 + 150 + xOffset, 2 + 78, 0xefefef);
        }

        if (recipe.getLoops() > 1) {
            graphics.pose().pushPose();
            graphics.pose().translate(15, 9, 0);
            AllIcons.I_SEQ_REPEAT.render(graphics, 50 + xOffset, 75);
            Component repeat = Component.literal("x" + recipe.getLoops());
            graphics.drawString(font, repeat, 66 + xOffset, 80, 0x888888, false);
            graphics.pose().popPose();
        }

        graphics.pose().popPose();

        List<SequencedRecipe<?>> sequence = recipe.getSequence();
        int totalSteps = sequence.size();
        int currentPage = SequencedAssemblyPageManager.getCurrentPage(recipe);
        int stepsPerPage = SequencedAssemblyPageManager.getStepsPerPage();
        int startIndex = currentPage * stepsPerPage;
        int endIndex = Math.min(startIndex + stepsPerPage, totalSteps);
        
        // Calculate total pages once
        int totalPages = (int) Math.ceil(totalSteps / (double) stepsPerPage);

        // Calculate width of current page for centering
        int pageWidth = 0;
        for (int i = startIndex; i < endIndex; i++) {
            SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequence.get(i));
            pageWidth += subCategory.getWidth() + STEP_MARGIN;
        }
        if (pageWidth > 0) {
            pageWidth -= STEP_MARGIN;
        }

        IDrawable background = getBackgroundHelper();
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int x = bgWidth / 2 - pageWidth / 2;

        graphics.pose().pushPose();
        graphics.pose().translate(x, 0, 0);

        // Draw steps for current page
        for (int i = startIndex; i < endIndex; i++) {
            SequencedRecipe<?> sequencedRecipe = sequence.get(i);
            SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequencedRecipe);
            int subWidth = subCategory.getWidth();
            // Cache component creation - reuse string index
            int romanIndex = Math.min(i, EXTENDED_ROMANS.length - 1);
            MutableComponent component = Component.literal(EXTENDED_ROMANS[romanIndex]);
            graphics.drawString(font, component, font.width(component) / -2 + subWidth / 2, 2, 0x888888, false);
            subCategory.draw(sequencedRecipe, graphics, mouseX - x, mouseY, i);
            graphics.pose().translate(subWidth + STEP_MARGIN, 0, 0);
        }

        graphics.pose().popPose();

        // Draw page indicator in bottom-right corner, flush with edges
        if (totalPages > 1) {
            Component pageIndicator = Component.literal((currentPage + 1) + "/" + totalPages);
            // Position flush with right and bottom edges
            int indicatorX = bgWidth - font.width(pageIndicator); // Flush with right edge
            int indicatorY = bgHeight - font.lineHeight; // Flush with bottom edge
            graphics.drawString(font, pageIndicator, indicatorX, indicatorY, 0x888888, false);
        }

        graphics.pose().popPose();
    }

    /**
     * Overwrite getTooltipStrings for pagination.
     */
    @Overwrite(remap = false)
    public List<Component> getTooltipStrings(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltip = new ArrayList<>();

        MutableComponent junk = CreateLang.translateDirect("recipe.assembly.junk");

        boolean singleOutput = recipe.getOutputChance() == 1;
        boolean willRepeat = recipe.getLoops() > 1;

        int xOffset = -7;
        int minX = 150 + xOffset;
        int maxX = minX + 18;
        int minY = 90;
        int maxY = minY + 18;
        if (!singleOutput && mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
            float chance = recipe.getOutputChance();
            tooltip.add(junk);
            tooltip.add(invokeChanceComponent(1 - chance));
            return tooltip;
        }

        minX = 55 + xOffset;
        maxX = minX + 65;
        minY = 92;
        maxY = minY + 24;
        if (willRepeat && mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
            tooltip.add(CreateLang.translateDirect("recipe.assembly.repeat", recipe.getLoops()));
            return tooltip;
        }

        List<SequencedRecipe<?>> sequence = recipe.getSequence();
        int totalSteps = sequence.size();
        int currentPage = SequencedAssemblyPageManager.getCurrentPage(recipe);
        int stepsPerPage = SequencedAssemblyPageManager.getStepsPerPage();
        int startIndex = currentPage * stepsPerPage;
        int endIndex = Math.min(startIndex + stepsPerPage, totalSteps);

        // Calculate width of current page for centering
        int pageWidth = 0;
        for (int i = startIndex; i < endIndex; i++) {
            SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequence.get(i));
            pageWidth += subCategory.getWidth() + STEP_MARGIN;
        }
        if (pageWidth > 0) {
            pageWidth -= STEP_MARGIN;
        }

        int bgWidth = getBackgroundHelper().getWidth();
        int pageX = bgWidth / 2 - pageWidth / 2;

        // Check if mouse is over a step on current page
        double relativeX = mouseX - pageX;
        for (int i = startIndex; i < endIndex; i++) {
            SequencedRecipe<?> sequencedRecipe = sequence.get(i);
            SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequencedRecipe);
            if (relativeX >= 0 && relativeX < subCategory.getWidth() && mouseY >= 0 && mouseY < 25) {
                tooltip.add(CreateLang.translateDirect("recipe.assembly.step", i + 1));
                tooltip.add(sequencedRecipe.getAsAssemblyRecipe().getDescriptionForAssembly().plainCopy().withStyle(ChatFormatting.DARK_GREEN));
                return tooltip;
            }
            relativeX -= subCategory.getWidth() + STEP_MARGIN;
        }

        return tooltip;
    }


}
