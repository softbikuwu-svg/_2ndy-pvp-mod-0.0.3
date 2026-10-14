package com.secondy.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    public static final List<Module> ALL = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        if (!ALL.isEmpty()) return;

        // HUD / information
        ALL.add(new FpsModule());
        ALL.add(new CpsModule());
        ALL.add(new KeystrokesModule());
        ALL.add(new CoordsModule());
        ALL.add(new ArmorModule());
        ALL.add(new Ping());
        ALL.add(new Direction());
        ALL.add(new Biome());
        ALL.add(new Speed());
        ALL.add(new TargetInfo());
        ALL.add(new TargetHealth());
        ALL.add(new AttackCooldown());
        ALL.add(new InventoryCounter());
        ALL.add(new Effects());
        ALL.add(new MobCounter());
        ALL.add(new SessionTime());
        ALL.add(new Clock());
        ALL.add(new Memory());
        ALL.add(new WorldTime());
        ALL.add(new ArmorDurability());
        ALL.add(new FrameTime());
        ALL.add(new CoordinateDetails());
        ALL.add(new Health());
        ALL.add(new Hunger());
        ALL.add(new Experience());
        ALL.add(new HeldItem());
        ALL.add(new LightLevel());
        ALL.add(new ChunkPosition());
        ALL.add(new Dimension());
        ALL.add(new Weather());
        ALL.add(new EntityDistance());
        ALL.add(new FallDistance());
        ALL.add(new Air());
        ALL.add(new FireState());
        ALL.add(new Underwater());
        ALL.add(new SelectedSlot());
        ALL.add(new MainHandDurability());
        ALL.add(new GameMode());
        ALL.add(new BlockInfo());
        ALL.add(new TargetName());
        ALL.add(new Velocity());
        ALL.add(new OnGround());
        ALL.add(new CombatTimer());
        ALL.add(new Crosshair());
        ALL.add(new FpsMode());
        ALL.add(new CoordinatesNether());
        ALL.add(new FoodHeld());

        // PvP
        ALL.add(new ComboModule());
        ALL.add(new ReachModule());

        // Utility
        ALL.add(new SprintModule());
        ALL.add(new ZoomModule());
        ALL.add(new ServerInfo());

        // Visual
        ALL.add(new FullbrightModule());
        ALL.add(new ChromaModule());

        // Performance
        // Informational modules above are deliberately lightweight and event-free.
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> type) {
        for (Module m : ALL) if (type.isInstance(m)) return (T) m;
        throw new IllegalStateException("Module not found: " + type.getSimpleName());
    }

    public static List<HudModule> huds() {
        List<HudModule> list = new ArrayList<>();
        for (Module m : ALL) if (m instanceof HudModule h) list.add(h);
        return list;
    }

    public static void tick(MinecraftClient mc) {
        for (Module m : ALL) if (m.isEnabled()) m.onTick(mc);
    }

    public static void renderHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.options.hudHidden) return;
        for (HudModule h : huds()) if (h.isEnabled()) h.draw(ctx, mc);
    }
}
