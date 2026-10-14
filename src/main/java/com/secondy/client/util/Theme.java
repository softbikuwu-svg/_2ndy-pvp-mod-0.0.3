package com.secondy.client.util;

import net.minecraft.client.gui.DrawContext;

public final class Theme {
    private Theme() {}

    public static boolean chroma = false;
    public static final int BG = 0xE809090D;
    public static final int PANEL = 0xF0121218;
    public static final int PANEL_2 = 0xF0181820;
    public static final int BORDER = 0xFF2D2D38;
    public static final int ACCENT = 0xFF5B8CFF;
    public static final int TEXT = 0xFFF4F4F7;
    public static final int MUTED = 0xFF9696A3;

    public static int color(float offset) {
        if (!chroma) return TEXT;
        float h = ((System.currentTimeMillis() % 4000L) / 4000f + offset) % 1f;
        return java.awt.Color.HSBtoRGB(h, 0.70f, 1f) | 0xFF000000;
    }

    public static void outline(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void panel(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + h, PANEL);
        outline(ctx, x, y, w, h, BORDER);
    }
}
