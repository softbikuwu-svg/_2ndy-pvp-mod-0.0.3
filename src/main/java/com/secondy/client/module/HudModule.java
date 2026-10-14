package com.secondy.client.module;

import com.secondy.client.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class HudModule extends Module {
    public int x, y;
    protected int width = 60, height = 14;
    private boolean backgroundEnabled = true;
    private int backgroundOpacity = 72;

    protected HudModule(String name, String description, Category category, int x, int y) {
        super(name, description, category);
        this.x = x;
        this.y = y;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean hasBackground() { return backgroundEnabled; }
    public void setBackgroundEnabled(boolean value) { backgroundEnabled = value; }
    public int getBackgroundOpacity() { return backgroundOpacity; }
    public void setBackgroundOpacity(int value) {
        backgroundOpacity = Math.max(0, Math.min(255, value));
    }

    public boolean contains(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public abstract void draw(DrawContext ctx, MinecraftClient mc);

    protected void background(DrawContext ctx) {
        if (!backgroundEnabled) return;
        int alpha = Math.max(0, Math.min(255, backgroundOpacity));
        ctx.fill(x, y, x + width, y + height, (alpha << 24) | 0x00101014);
    }

    protected void label(DrawContext ctx, MinecraftClient mc, String text) {
        width = Math.max(30, mc.textRenderer.getWidth(text) + 10);
        height = 16;
        background(ctx);
        ctx.drawText(mc.textRenderer, text, x + 5, y + 4, Theme.color(0f), true);
    }
}
