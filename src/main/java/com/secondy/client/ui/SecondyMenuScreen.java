package com.secondy.client.ui;

import com.secondy.client.module.Category;
import com.secondy.client.module.HudModule;
import com.secondy.client.module.Module;
import com.secondy.client.module.ModuleManager;
import com.secondy.client.util.Config;
import com.secondy.client.util.Input;
import com.secondy.client.util.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal Windows 11-inspired dark client menu.
 * Left click toggles modules. Right click on a HUD module toggles its background.
 */
public class SecondyMenuScreen extends Screen {
    private static final int PW = 620, PH = 390;
    private Category selected = Category.HUD;
    private boolean wasLeft, wasRight, shiftWasDown;
    private int scroll;

    public SecondyMenuScreen() { super(Text.literal("_2ndy Client")); }

    @Override
    protected void init() {
        wasLeft = Input.mouse(0);
        wasRight = Input.mouse(1);
        shiftWasDown = Input.key(GLFW.GLFW_KEY_RIGHT_SHIFT);
        scroll = 0;
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void close() {
        Config.save();
        super.close();
    }

    private List<Module> modules() {
        List<Module> list = new ArrayList<>();
        for (Module m : ModuleManager.ALL) if (m.category == selected) list.add(m);
        return list;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        // Darkened desktop-style backdrop
        ctx.fill(0, 0, width, height, 0xA5000000);

        int px = (width - PW) / 2;
        int py = (height - PH) / 2;
        Theme.panel(ctx, px, py, PW, PH);

        // Header
        ctx.fill(px, py, px + PW, py + 46, 0xFF101014);
        ctx.drawText(textRenderer, "_2NDY CLIENT", px + 18, py + 10, Theme.color(0f), true);
        ctx.drawText(textRenderer, "PvP • PvE • HUD • Visual • Performance",
                px + 18, py + 26, Theme.MUTED, false);
        String close = "Right Shift  •  close";
        ctx.drawText(textRenderer, close, px + PW - 18 - textRenderer.getWidth(close), py + 17, Theme.MUTED, false);

        // Sidebar
        int sideX = px + 10, sideY = py + 56, sideW = 126;
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            int ty = sideY + i * 42;
            boolean hover = in(mx, my, sideX, ty, sideW, 36);
            int bg = cats[i] == selected ? Theme.ACCENT : (hover ? 0xFF202027 : 0xFF15151B);
            ctx.fill(sideX, ty, sideX + sideW, ty + 36, bg);
            String t = cats[i].label;
            ctx.drawText(textRenderer, t, sideX + 12, ty + 13, Theme.TEXT, false);
        }

        // Main module grid
        int contentX = px + 150, contentY = py + 56;
        int contentW = PW - 166;
        ctx.fill(contentX, contentY, contentX + contentW, py + PH - 38, 0xFF0D0D12);

        List<Module> list = modules();
        int cardW = (contentW - 22) / 2;
        int cardH = 54;
        int visibleTop = contentY + 6;
        int visibleBottom = py + PH - 44;
        int maxScroll = Math.max(0, ((list.size() + 1) / 2) * 60 - (visibleBottom - visibleTop));
        scroll = Math.max(0, Math.min(scroll, maxScroll));

        String hint = "Left click: toggle    Right click: HUD background";
        boolean left = Input.mouse(0), right = Input.mouse(1);
        boolean clickedLeft = left && !wasLeft;
        boolean clickedRight = right && !wasRight;

        for (int i = 0; i < list.size(); i++) {
            Module m = list.get(i);
            int col = i % 2, row = i / 2;
            int rx = contentX + 6 + col * (cardW + 10);
            int ry = visibleTop + row * 60 - scroll;
            if (ry + cardH < visibleTop || ry > visibleBottom) continue;

            boolean hover = in(mx, my, rx, ry, cardW, cardH);
            ctx.fill(rx, ry, rx + cardW, ry + cardH, hover ? 0xFF1C1C24 : 0xFF15151B);
            Theme.outline(ctx, rx, ry, cardW, cardH, hover ? 0xFF3C3C49 : 0xFF25252D);

            ctx.drawText(textRenderer, m.name, rx + 10, ry + 8, Theme.TEXT, false);
            ctx.drawText(textRenderer, trim(m.description, cardW - 80), rx + 10, ry + 24, Theme.MUTED, false);

            String state = m.isEnabled() ? "ON" : "OFF";
            int sw = 34;
            int sx = rx + cardW - sw - 8;
            ctx.fill(sx, ry + 8, sx + sw, ry + 25, m.isEnabled() ? 0xFF315FBA : 0xFF292931);
            ctx.drawText(textRenderer, state, sx + (sw - textRenderer.getWidth(state)) / 2, ry + 13,
                    Theme.TEXT, false);

            if (m instanceof HudModule h) {
                String bg = h.hasBackground() ? "BG" : "NO BG";
                ctx.drawText(textRenderer, bg, rx + cardW - 10 - textRenderer.getWidth(bg), ry + 38,
                        h.hasBackground() ? Theme.MUTED : 0xFF6BA7FF, false);
            }

            if (hover) {
                hint = m instanceof HudModule
                        ? "Left click: toggle    Right click: toggle this HUD background"
                        : "Left click: toggle";
                if (clickedLeft) { m.toggle(); Config.save(); }
                if (clickedRight && m instanceof HudModule h) {
                    h.setBackgroundEnabled(!h.hasBackground());
                    Config.save();
                }
            }
        }

        // Sidebar click after cards so the interaction remains simple.
        for (int i = 0; i < cats.length; i++) {
            int ty = sideY + i * 42;
            if (clickedLeft && in(mx, my, sideX, ty, sideW, 36)) {
                selected = cats[i];
                scroll = 0;
            }
        }

        ctx.drawText(textRenderer, hint, px + 150, py + PH - 28, Theme.MUTED, false);
        String count = list.size() + " modules";
        ctx.drawText(textRenderer, count, px + PW - 18 - textRenderer.getWidth(count), py + PH - 28, Theme.MUTED, false);

        if (shiftWasDown && !Input.key(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            // Do not close on release; opening/closing is handled by the normal key edge in the client.
        }
        shiftWasDown = Input.key(GLFW.GLFW_KEY_RIGHT_SHIFT);
        wasLeft = left;
        wasRight = right;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll -= (int) (verticalAmount * 48);
        return true;
    }

    private String trim(String s, int maxWidth) {
        if (textRenderer.getWidth(s) <= maxWidth) return s;
        while (s.length() > 3 && textRenderer.getWidth(s + "...") > maxWidth) s = s.substring(0, s.length() - 1);
        return s + "...";
    }

    private static boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
