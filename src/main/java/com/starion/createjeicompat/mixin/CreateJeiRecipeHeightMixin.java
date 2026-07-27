package com.starion.createjeicompat.mixin;

import com.simibubi.create.compat.jei.CreateJEI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Grow the real Create JEI {@code emptyBackground} by 1px so page controls clear the row above.
 * Must change the drawable size (not only {@code getBackground().getHeight()}).
 * Priority after {@link CreateJeiScrapPaddingMixin} when Cyber Goggles is present.
 */
@Mixin(value = CreateJEI.class, priority = 2100, remap = false)
public abstract class CreateJeiRecipeHeightMixin {

    @ModifyArg(
            method = "loadCategories",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/compat/jei/CreateJEI$CategoryBuilder;emptyBackground(II)"
                            + "Lcom/simibubi/create/compat/jei/CreateJEI$CategoryBuilder;"
            ),
            index = 1
    )
    private int createjeicompat$onePixelTaller(int height) {
        return height + 1;
    }
}
