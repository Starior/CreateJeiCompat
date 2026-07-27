package com.starion.createjeicompat.mixin;

import com.simibubi.create.compat.jei.CreateJEI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * CreateCyberGoggles adds {@code +40} to every Create JEI category for two scrap rows of 8.
 * We lay scrap out in one row of 9, so peel that down to a single-row pad after CCG runs.
 */
@Mixin(value = CreateJEI.class, priority = 2000, remap = false)
public abstract class CreateJeiScrapPaddingMixin {

    private static final int CREATE_DEFAULT_HEIGHT = 115;
    private static final int CCG_TWO_ROW_PAD = 40;
    private static final int ONE_ROW_PAD = 24;

    @ModifyArg(
            method = "loadCategories",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/compat/jei/CreateJEI$CategoryBuilder;emptyBackground(II)"
                            + "Lcom/simibubi/create/compat/jei/CreateJEI$CategoryBuilder;"
            ),
            index = 1
    )
    private int createjeicompat$oneScrapRowHeight(int height) {
        if (height >= CREATE_DEFAULT_HEIGHT + CCG_TWO_ROW_PAD) {
            return CREATE_DEFAULT_HEIGHT + ONE_ROW_PAD;
        }
        return height;
    }
}
