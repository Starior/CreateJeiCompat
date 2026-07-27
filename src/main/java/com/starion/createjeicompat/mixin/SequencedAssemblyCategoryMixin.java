package com.starion.createjeicompat.mixin;

/**
 * Pagination for Create's SequencedAssemblyCategory (JEI), also used by EMI via JEmi.
 *
 * Uses MixinExtras {@code @WrapMethod} (not {@code @Overwrite}). Other mods' injects into
 * {@code setRecipe} live inside {@code original}; we do not call it (would duplicate Create's layout).
 * NeoForge Cyber Goggles scrap uses the junk {@code ?} slot (not a separate scrap row) — no CCG scrap redraw here.
 *
 * Page changes: mouse wheel (JEI + EMI), on-screen {@code < 1/N >} controls, and
 * Up/Down/Left/Right keys (JEI + EMI both forward those through {@code IRecipeCategory#handleInput}).
 *
 * Original Create mod code: https://github.com/Creators-of-Create/Create
 * JEI: https://github.com/mezz/JustEnoughItems
 * EMI: https://github.com/emilyploszaj/emi
 */
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.SequencedAssemblyCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import com.starion.createjeicompat.PageControls;
import com.starion.createjeicompat.RecipeViewerRefresh;
import com.starion.createjeicompat.SequencedAssemblyPageManager;
import com.starion.createjeicompat.config.CJCConfigs;
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
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mixin(value = SequencedAssemblyCategory.class, remap = false)
public abstract class SequencedAssemblyCategoryMixin {

    @Unique
    private static final int STEP_MARGIN = 3;

    @Unique
    private static String toRomanNumeral(int number) {
        if (number < 1 || number > 3999) {
            return String.valueOf(number);
        }
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return thousands[number / 1000]
                + hundreds[(number % 1000) / 100]
                + tens[(number % 100) / 10]
                + ones[number % 10];
    }

    @Shadow
    Map<ResourceLocation, SequencedAssemblySubCategory> subCategories;

    @Invoker("getSubCategory")
    abstract SequencedAssemblySubCategory invokeGetSubCategory(SequencedRecipe<?> sequencedRecipe);

    @Invoker("chanceComponent")
    abstract MutableComponent invokeChanceComponent(float chance);

    @Unique
    private IDrawable cachedBackground;

    @Unique
    private IDrawable getBackgroundHelper() {
        if (cachedBackground == null) {
            cachedBackground = ((CreateRecipeCategory<?>) (Object) this).getBackground();
        }
        return cachedBackground;
    }

    @Unique
    private void registerInvisible(IRecipeLayoutBuilder builder, SequencedRecipe<?> sequencedRecipe) {
        NonNullList<Ingredient> sequencedIngredients = sequencedRecipe.getRecipe().getIngredients();
        for (Ingredient ingredient : sequencedIngredients.subList(1, sequencedIngredients.size())) {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(ingredient);
        }
        for (SizedFluidIngredient fluidIngredient : sequencedRecipe.getRecipe().getFluidIngredients()) {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.asList(fluidIngredient.getFluids()));
        }
    }

    @WrapMethod(
            method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V"
    )
    private void createjeicompat$wrapSetRecipe(IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses, Operation<Void> original) {
        boolean noRandomOutput = recipe.getOutputChance() == 1;
        int xOffset = noRandomOutput ? 0 : -7;

        builder
                .addSlot(RecipeIngredientRole.INPUT, 27 + xOffset, 91)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addItemStacks(List.of(recipe.getIngredient().getItems()));
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 132 + xOffset, 91)
                .setBackground(CreateRecipeCategory.getRenderedSlot(recipe.getOutputChance()), -1, -1)
                .addItemStack(CreateRecipeCategory.getResultItem(recipe))
                .addTooltipCallback((recipeSlotView, tooltip) -> {
                    if (noRandomOutput) {
                        return;
                    }
                    tooltip.add(1, invokeChanceComponent(recipe.getOutputChance()));
                });

        List<SequencedRecipe<?>> sequence = recipe.getSequence();
        int totalSteps = sequence.size();
        int currentPage = SequencedAssemblyPageManager.getCurrentPage(recipe);
        int stepsPerPage = SequencedAssemblyPageManager.getStepsPerPage();
        int startIndex = currentPage * stepsPerPage;
        int endIndex = Math.min(startIndex + stepsPerPage, totalSteps);

        int pageWidth = 0;
        for (int i = startIndex; i < endIndex; i++) {
            pageWidth += invokeGetSubCategory(sequence.get(i)).getWidth() + STEP_MARGIN;
        }
        if (pageWidth > 0) {
            pageWidth -= STEP_MARGIN;
        }

        int x = getBackgroundHelper().getWidth() / 2 - pageWidth / 2;
        for (int i = 0; i < totalSteps; i++) {
            SequencedRecipe<?> sequencedRecipe = sequence.get(i);
            if (i >= startIndex && i < endIndex) {
                SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequencedRecipe);
                subCategory.setRecipe(builder, sequencedRecipe, focuses, x);
                x += subCategory.getWidth() + STEP_MARGIN;
            } else {
                registerInvisible(builder, sequencedRecipe);
            }
        }

        for (int i = 1; i < recipe.getLoops(); i++) {
            for (SequencedRecipe<?> sequencedRecipe : sequence) {
                registerInvisible(builder, sequencedRecipe);
            }
        }
    }

    @WrapMethod(
            method = "draw(Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V"
    )
    private void createjeicompat$wrapDraw(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY, Operation<Void> original) {
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
        int totalPages = SequencedAssemblyPageManager.getTotalPages(recipe);

        int pageWidth = 0;
        for (int i = startIndex; i < endIndex; i++) {
            pageWidth += invokeGetSubCategory(sequence.get(i)).getWidth() + STEP_MARGIN;
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

        for (int i = startIndex; i < endIndex; i++) {
            SequencedRecipe<?> sequencedRecipe = sequence.get(i);
            SequencedAssemblySubCategory subCategory = invokeGetSubCategory(sequencedRecipe);
            int subWidth = subCategory.getWidth();
            MutableComponent component = Component.literal(toRomanNumeral(i + 1));
            graphics.drawString(font, component, font.width(component) / -2 + subWidth / 2, 2, 0x888888, false);
            subCategory.draw(sequencedRecipe, graphics, mouseX - x, mouseY, i);
            graphics.pose().translate(subWidth + STEP_MARGIN, 0, 0);
        }

        graphics.pose().popPose();

        if (totalPages > 1) {
            drawPageControls(graphics, font, bgWidth, bgHeight, currentPage, totalPages, mouseX, mouseY);
        }

        graphics.pose().popPose();
    }

    /** Enabled/page text are dark; hover lightens a bit; disabled stays translucent. */
    @Unique
    private static final int PAGE_BTN_ENABLED = 0xFF666666;
    @Unique
    private static final int PAGE_BTN_HOVER = 0xFF999999;
    @Unique
    private static final int PAGE_BTN_DISABLED = 0x40666666;
    @Unique
    private static final int PAGE_TEXT_COLOR = 0xFF555555;

    @Unique
    private void drawPageControls(GuiGraphics graphics, Font font, int bgWidth, int bgHeight,
                                  int currentPage, int totalPages, double mouseX, double mouseY) {
        boolean showArrows = CJCConfigs.showPageArrows();
        PageControls c = new PageControls(font, bgWidth, bgHeight, currentPage, totalPages, showArrows);

        graphics.drawString(font, c.pageText, c.pageX, c.y, PAGE_TEXT_COLOR, false);

        if (!showArrows) {
            return;
        }

        boolean hoverPrev = c.canPrev && isOver(mouseX, mouseY, c.prevX, c.y, c.prevW, c.h);
        boolean hoverNext = c.canNext && isOver(mouseX, mouseY, c.nextX, c.y, c.nextW, c.h);

        int prevColor = c.canPrev ? (hoverPrev ? PAGE_BTN_HOVER : PAGE_BTN_ENABLED) : PAGE_BTN_DISABLED;
        int nextColor = c.canNext ? (hoverNext ? PAGE_BTN_HOVER : PAGE_BTN_ENABLED) : PAGE_BTN_DISABLED;

        graphics.drawString(font, c.prev, c.prevX, c.y, prevColor, false);
        graphics.drawString(font, c.next, c.nextX, c.y, nextColor, false);
    }

    @Unique
    private static boolean isOver(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    /**
     * Parameter type must be {@link Object} (erased {@code IRecipeCategory} signature).
     * A mixin-merged method does not get javac's synthetic bridge for a typed recipe param.
     */
    public boolean handleInput(Object recipeObj, double mouseX, double mouseY, InputConstants.Key input) {
        if (!(recipeObj instanceof SequencedAssemblyRecipe recipe)) {
            return false;
        }

        if (input.getType() == InputConstants.Type.KEYSYM) {
            int key = input.getValue();
            if (key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_LEFT) {
                boolean changed = SequencedAssemblyPageManager.previousPage(recipe);
                if (changed) {
                    RecipeViewerRefresh.schedule();
                }
                return changed;
            }
            if (key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_RIGHT) {
                boolean changed = SequencedAssemblyPageManager.nextPage(recipe);
                if (changed) {
                    RecipeViewerRefresh.schedule();
                }
                return changed;
            }
            return false;
        }

        if (input.getType() != InputConstants.Type.MOUSE || input.getValue() != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        if (!CJCConfigs.showPageArrows()) {
            return false;
        }

        int totalPages = SequencedAssemblyPageManager.getTotalPages(recipe);
        if (totalPages <= 1) {
            return false;
        }

        Font font = Minecraft.getInstance().font;
        IDrawable background = getBackgroundHelper();
        PageControls c = new PageControls(
                font,
                background.getWidth(),
                background.getHeight(),
                SequencedAssemblyPageManager.getCurrentPage(recipe),
                totalPages,
                true
        );

        if (c.canPrev && isOver(mouseX, mouseY, c.prevX, c.y, c.prevW, c.h)) {
            boolean changed = SequencedAssemblyPageManager.previousPage(recipe);
            if (changed) {
                RecipeViewerRefresh.schedule();
            }
            return changed;
        }
        if (c.canNext && isOver(mouseX, mouseY, c.nextX, c.y, c.nextW, c.h)) {
            boolean changed = SequencedAssemblyPageManager.nextPage(recipe);
            if (changed) {
                RecipeViewerRefresh.schedule();
            }
            return changed;
        }
        return false;
    }

    @WrapMethod(
            method = "getTooltipStrings(Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;DD)Ljava/util/List;"
    )
    private List<Component> createjeicompat$wrapGetTooltipStrings(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, double mouseX, double mouseY, Operation<List<Component>> original) {
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
            tooltip.add(junk);
            tooltip.add(invokeChanceComponent(1 - recipe.getOutputChance()));
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

        int totalPages = SequencedAssemblyPageManager.getTotalPages(recipe);
        if (totalPages > 1 && CJCConfigs.showPageArrows()) {
            Font font = Minecraft.getInstance().font;
            IDrawable background = getBackgroundHelper();
            PageControls c = new PageControls(
                    font,
                    background.getWidth(),
                    background.getHeight(),
                    SequencedAssemblyPageManager.getCurrentPage(recipe),
                    totalPages,
                    true
            );
            if (c.canPrev && isOver(mouseX, mouseY, c.prevX, c.y, c.prevW, c.h)) {
                tooltip.add(Component.literal("Previous page"));
                return tooltip;
            }
            if (c.canNext && isOver(mouseX, mouseY, c.nextX, c.y, c.nextW, c.h)) {
                tooltip.add(Component.literal("Next page"));
                return tooltip;
            }
        }

        List<SequencedRecipe<?>> sequence = recipe.getSequence();
        int currentPage = SequencedAssemblyPageManager.getCurrentPage(recipe);
        int stepsPerPage = SequencedAssemblyPageManager.getStepsPerPage();
        int startIndex = currentPage * stepsPerPage;
        int endIndex = Math.min(startIndex + stepsPerPage, sequence.size());

        int pageWidth = 0;
        for (int i = startIndex; i < endIndex; i++) {
            pageWidth += invokeGetSubCategory(sequence.get(i)).getWidth() + STEP_MARGIN;
        }
        if (pageWidth > 0) {
            pageWidth -= STEP_MARGIN;
        }

        int pageX = getBackgroundHelper().getWidth() / 2 - pageWidth / 2;
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
