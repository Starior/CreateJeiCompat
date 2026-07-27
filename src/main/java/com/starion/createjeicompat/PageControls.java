package com.starion.createjeicompat;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * Layout for horizontal sequenced-assembly page controls: {@code < 1/N >} or just {@code 1/N}.
 * Kept outside the mixin package — Mixin forbids loading nested types from mixin packages.
 */
public final class PageControls {
    public static final int GAP = 3;

    public final Component prev = Component.literal("<");
    public final Component next = Component.literal(">");
    public final Component pageText;
    public final int y;
    public final int prevX;
    public final int pageX;
    public final int nextX;
    public final int prevW;
    public final int nextW;
    public final int h;
    public final boolean canPrev;
    public final boolean canNext;
    public final boolean showArrows;

    public PageControls(Font font, int bgWidth, int bgHeight, int currentPage, int totalPages, boolean showArrows) {
        this.pageText = Component.literal((currentPage + 1) + "/" + totalPages);
        this.h = font.lineHeight;
        this.y = bgHeight - this.h + 2;
        this.showArrows = showArrows;
        this.prevW = font.width(prev);
        this.nextW = font.width(next);
        int pageW = font.width(pageText);
        if (showArrows) {
            int totalW = prevW + GAP + pageW + GAP + nextW;
            this.prevX = bgWidth - totalW;
            this.pageX = prevX + prevW + GAP;
            this.nextX = pageX + pageW + GAP;
        } else {
            this.prevX = 0;
            this.nextX = 0;
            this.pageX = bgWidth - pageW;
        }
        this.canPrev = currentPage > 0;
        this.canNext = currentPage < totalPages - 1;
    }
}
