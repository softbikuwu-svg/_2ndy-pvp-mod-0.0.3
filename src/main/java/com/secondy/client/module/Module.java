package com.secondy.client.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }
    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
}
