package com.secondy.client.module;

public enum Category {
    HUD("HUD"),
    PVP("PvP"),
    PVE("PvE"),
    UTILITY("Utility"),
    VISUAL("Visual"),
    PERFORMANCE("Performance");

    public final String label;
    Category(String label) { this.label = label; }
}
