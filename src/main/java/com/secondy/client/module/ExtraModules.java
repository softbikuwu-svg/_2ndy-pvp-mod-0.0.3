package com.secondy.client.module;

import com.secondy.client.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Items;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Extra client-side information modules. They are intentionally informational:
 * no combat automation, packet manipulation, or server-side advantages.
 */
public final class ExtraModules {
    private ExtraModules() {}

    static abstract class TextHud extends HudModule {
        TextHud(String name, String description, Category category, int x, int y) {
            super(name, description, category, x, y);
        }
        protected abstract String text(MinecraftClient mc);
        @Override public void draw(DrawContext ctx, MinecraftClient mc) { label(ctx, mc, text(mc)); }
    }

    public static class Ping extends TextHud {
        public Ping() { super("Ping", "Shows your current server latency.", Category.PVP, 6, 116); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null || mc.getNetworkHandler() == null) return "Ping: -";
            var e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            return "Ping: " + (e == null ? "-" : e.getLatency() + " ms");
        }
    }

    public static class Direction extends TextHud {
        public Direction() { super("Direction", "Shows your current facing direction.", Category.HUD, 6, 136); }
        protected String text(MinecraftClient mc) {
            return mc.player == null ? "Direction: -" : "Direction: " + mc.player.getHorizontalFacing().getName().toUpperCase(Locale.ROOT);
        }
    }

    public static class Biome extends TextHud {
        public Biome() { super("Biome", "Shows the biome you are currently standing in.", Category.PVE, 6, 156); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null || mc.world == null) return "Biome: -";
            return "Biome: " + mc.world.getBiome(mc.player.getBlockPos()).getKey()
                    .map(k -> k.getValue().getPath()).orElse("unknown");
        }
    }

    public static class ServerInfo extends TextHud {
        public ServerInfo() { super("Server Info", "Shows the current multiplayer server address.", Category.UTILITY, 6, 176); }
        protected String text(MinecraftClient mc) {
            var entry = mc.getCurrentServerEntry();
            return entry == null ? "Server: Singleplayer" : "Server: " + entry.address;
        }
    }

    public static class Speed extends TextHud {
        public Speed() { super("Speed", "Shows horizontal movement speed in blocks per second.", Category.PVP, 120, 116); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Speed: -";
            double dx = mc.player.getVelocity().x;
            double dz = mc.player.getVelocity().z;
            return String.format(Locale.ROOT, "Speed: %.2f b/s", Math.sqrt(dx * dx + dz * dz) * 20.0);
        }
    }

    public static class TargetInfo extends TextHud {
        public TargetInfo() { super("Target Info", "Shows health and distance for the entity under your crosshair.", Category.PVP, 120, 136); }
        protected String text(MinecraftClient mc) {
            if (!(mc.targetedEntity instanceof LivingEntity e) || mc.player == null) return "Target: -";
            return String.format(Locale.ROOT, "Target: %.1f HP | %.1fm", e.getHealth(), mc.player.distanceTo(e));
        }
    }

    public static class TargetHealth extends TextHud {
        public TargetHealth() { super("Target Health", "Displays the health of the entity under your crosshair.", Category.PVP, 120, 156); }
        protected String text(MinecraftClient mc) {
            if (!(mc.targetedEntity instanceof LivingEntity e)) return "Target HP: -";
            return String.format(Locale.ROOT, "Target HP: %.1f / %.1f", e.getHealth(), e.getMaxHealth());
        }
    }

    public static class AttackCooldown extends TextHud {
        public AttackCooldown() { super("Attack Cooldown", "Displays your vanilla attack cooldown progress.", Category.PVP, 120, 176); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Cooldown: -";
            return String.format(Locale.ROOT, "Cooldown: %d%%", Math.round(mc.player.getAttackCooldownProgress(0.0f) * 100f));
        }
    }

    public static class InventoryCounter extends TextHud {
        public InventoryCounter() { super("Item Counter", "Counts useful items in your inventory.", Category.PVP, 6, 196); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Items: -";
            int pearls = count(mc, Items.ENDER_PEARL);
            int apples = count(mc, Items.GOLDEN_APPLE);
            int totems = count(mc, Items.TOTEM_OF_UNDYING);
            return "Items: P " + pearls + " | G " + apples + " | T " + totems;
        }
        private int count(MinecraftClient mc, net.minecraft.item.Item item) {
            int n = 0;
            for (var s : mc.player.getInventory().main) if (s.isOf(item)) n += s.getCount();
            for (var s : mc.player.getInventory().offHand) if (s.isOf(item)) n += s.getCount();
            return n;
        }
    }

    public static class Effects extends TextHud {
        public Effects() { super("Effects", "Shows the number of active status effects.", Category.PVE, 6, 216); }
        protected String text(MinecraftClient mc) {
            return mc.player == null ? "Effects: -" : "Effects: " + mc.player.getStatusEffects().size();
        }
    }

    public static class MobCounter extends TextHud {
        public MobCounter() { super("Mob Counter", "Counts loaded living entities and hostile mobs.", Category.PVE, 6, 236); }
        protected String text(MinecraftClient mc) {
            if (mc.world == null) return "Mobs: -";
            int living = 0, hostile = 0;
            for (var e : mc.world.getEntities()) {
                if (e instanceof LivingEntity) living++;
                if (e instanceof HostileEntity) hostile++;
            }
            return "Mobs: " + living + " | Hostile: " + hostile;
        }
    }

    public static class SessionTime extends TextHud {
        private long started = System.currentTimeMillis();
        public SessionTime() { super("Session Time", "Tracks time since the client session started.", Category.UTILITY, 250, 116); }
        protected String text(MinecraftClient mc) {
            Duration d = Duration.ofMillis(System.currentTimeMillis() - started);
            return String.format(Locale.ROOT, "Session: %02d:%02d:%02d", d.toHours(), d.toMinutesPart(), d.toSecondsPart());
        }
    }

    public static class Clock extends TextHud {
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        public Clock() { super("Clock", "Shows the local system time.", Category.UTILITY, 250, 136); }
        protected String text(MinecraftClient mc) { return "Time: " + LocalTime.now().format(fmt); }
    }

    public static class Memory extends TextHud {
        public Memory() { super("Memory", "Shows current Java heap usage.", Category.PERFORMANCE, 250, 156); }
        protected String text(MinecraftClient mc) {
            Runtime r = Runtime.getRuntime();
            long used = (r.totalMemory() - r.freeMemory()) / (1024 * 1024);
            long max = r.maxMemory() / (1024 * 1024);
            return "RAM: " + used + " / " + max + " MB";
        }
    }

    public static class WorldTime extends TextHud {
        public WorldTime() { super("World Time", "Shows the in-game day time.", Category.PVE, 250, 176); }
        protected String text(MinecraftClient mc) {
            return mc.world == null ? "World Time: -" : "World Time: " + (mc.world.getTimeOfDay() % 24000);
        }
    }

    public static class ArmorDurability extends TextHud {
        public ArmorDurability() { super("Armor Durability", "Compact armor durability summary.", Category.PVP, 250, 196); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Armor: -";
            int total = 0, max = 0;
            for (var s : mc.player.getInventory().getArmor()) {
                if (!s.isEmpty() && s.isDamageableItem()) {
                    total += s.getMaxDamage() - s.getDamage();
                    max += s.getMaxDamage();
                }
            }
            return max == 0 ? "Armor: -" : String.format(Locale.ROOT, "Armor: %d%%", Math.round(total * 100f / max));
        }
    }

    public static class FrameTime extends TextHud {
        public FrameTime() { super("Frame Time", "Estimates frame time from the current FPS.", Category.PERFORMANCE, 250, 216); }
        protected String text(MinecraftClient mc) {
            int fps = Math.max(1, mc.getCurrentFps());
            return String.format(Locale.ROOT, "Frame: %.2f ms", 1000.0 / fps);
        }
    }

    public static class CoordinateDetails extends TextHud {
        public CoordinateDetails() { super("Coordinate Details", "Shows block coordinates with facing.", Category.HUD, 250, 236); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Pos: -";
            return "Pos: " + mc.player.getBlockX() + " " + mc.player.getBlockY() + " " + mc.player.getBlockZ();
        }
    }

    public static class Health extends TextHud {
        public Health() { super("Health", "Shows current and maximum player health.", Category.HUD, 400, 116); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Health: -";
            return String.format(Locale.ROOT, "Health: %.1f / %.1f", mc.player.getHealth(), mc.player.getMaxHealth());
        }
    }

    public static class Hunger extends TextHud {
        public Hunger() { super("Hunger", "Shows food and saturation.", Category.PVE, 400, 136); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Hunger: -";
            return String.format(Locale.ROOT, "Food: %d | Sat: %.1f",
                    mc.player.getHungerManager().getFoodLevel(),
                    mc.player.getHungerManager().getSaturationLevel());
        }
    }

    public static class Experience extends TextHud {
        public Experience() { super("Experience", "Shows experience level and progress.", Category.PVE, 400, 156); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "XP: -";
            return String.format(Locale.ROOT, "XP: %d | %d%%", mc.player.experienceLevel,
                    Math.round(mc.player.experienceProgress * 100f));
        }
    }

    public static class HeldItem extends TextHud {
        public HeldItem() { super("Held Item", "Shows the name and count of the held item.", Category.UTILITY, 400, 176); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null || mc.player.getMainHandStack().isEmpty()) return "Held: -";
            var s = mc.player.getMainHandStack();
            return "Held: " + s.getName().getString() + " x" + s.getCount();
        }
    }

    public static class LightLevel extends TextHud {
        public LightLevel() { super("Light Level", "Shows block light level at your position.", Category.PVE, 400, 196); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null || mc.world == null) return "Light: -";
            return "Light: " + mc.world.getLightLevel(mc.player.getBlockPos());
        }
    }

    public static class ChunkPosition extends TextHud {
        public ChunkPosition() { super("Chunk Position", "Shows your current chunk coordinates.", Category.PVE, 400, 216); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Chunk: -";
            return "Chunk: " + (mc.player.getBlockX() >> 4) + ", " + (mc.player.getBlockZ() >> 4);
        }
    }

    public static class Dimension extends TextHud {
        public Dimension() { super("Dimension", "Shows the current Minecraft dimension.", Category.PVE, 400, 236); }
        protected String text(MinecraftClient mc) {
            if (mc.world == null) return "Dimension: -";
            return "Dimension: " + mc.world.getRegistryKey().getValue().getPath();
        }
    }

    public static class Weather extends TextHud {
        public Weather() { super("Weather", "Shows current world weather.", Category.VISUAL, 400, 256); }
        protected String text(MinecraftClient mc) {
            if (mc.world == null) return "Weather: -";
            return "Weather: " + (mc.world.isThundering() ? "Thunder" : mc.world.isRaining() ? "Rain" : "Clear");
        }
    }

    public static class EntityDistance extends TextHud {
        public EntityDistance() { super("Entity Distance", "Shows distance to the entity under your crosshair.", Category.PVP, 400, 276); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null || mc.targetedEntity == null) return "Entity: -";
            return String.format(Locale.ROOT, "Entity: %.2fm", mc.player.distanceTo(mc.targetedEntity));
        }
    }

    public static class FallDistance extends TextHud {
        public FallDistance() { super("Fall Distance", "Shows current falling distance.", Category.PVE, 400, 296); }
        protected String text(MinecraftClient mc) {
            return mc.player == null ? "Fall: -" : String.format(Locale.ROOT, "Fall: %.1f", mc.player.fallDistance);
        }
    }

    public static class Air extends TextHud {
        public Air() { super("Air", "Shows remaining underwater air.", Category.PVE, 400, 316); }
        protected String text(MinecraftClient mc) {
            return mc.player == null ? "Air: -" : "Air: " + mc.player.getAir();
        }
    }

    public static class FireState extends TextHud {
        public FireState() { super("Fire State", "Shows whether you are currently burning.", Category.PVE, 400, 336); }
        protected String text(MinecraftClient mc) { return mc.player != null && mc.player.isOnFire() ? "Fire: YES" : "Fire: NO"; }
    }

    public static class Underwater extends TextHud {
        public Underwater() { super("Underwater", "Shows whether you are submerged.", Category.PVE, 400, 356); }
        protected String text(MinecraftClient mc) { return mc.player != null && mc.player.isSubmergedInWater() ? "Underwater: YES" : "Underwater: NO"; }
    }

    public static class SelectedSlot extends TextHud {
        public SelectedSlot() { super("Selected Slot", "Shows your currently selected hotbar slot.", Category.UTILITY, 6, 256); }
        protected String text(MinecraftClient mc) {
            return mc.player == null ? "Slot: -" : "Slot: " + (mc.player.getInventory().getSelectedSlot() + 1);
        }
    }

    public static class MainHandDurability extends TextHud {
        public MainHandDurability() { super("Tool Durability", "Shows remaining durability of the held tool.", Category.PVE, 6, 276); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Tool: -";
            var s = mc.player.getMainHandStack();
            if (s.isEmpty() || !s.isDamageable()) return "Tool: -";
            return "Tool: " + (s.getMaxDamage() - s.getDamage());
        }
    }

    public static class GameMode extends TextHud {
        public GameMode() { super("Game Mode", "Shows the current player game mode.", Category.UTILITY, 6, 296); }
        protected String text(MinecraftClient mc) {
            if (mc.interactionManager == null) return "Mode: -";
            return "Mode: " + mc.interactionManager.getCurrentGameMode().getName();
        }
    }


    public static class BlockInfo extends TextHud {
        public BlockInfo() { super("Block Info", "Shows the block under your crosshair.", Category.PVE, 120, 256); }
        protected String text(MinecraftClient mc) {
            if (mc.world == null || mc.crosshairTarget == null || mc.crosshairTarget.getType() != net.minecraft.util.hit.HitResult.Type.BLOCK)
                return "Block: -";
            var hit = (net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget;
            return "Block: " + mc.world.getBlockState(hit.getBlockPos()).getBlock().getName().getString();
        }
    }

    public static class TargetName extends TextHud {
        public TargetName() { super("Target Name", "Shows the name of the entity under your crosshair.", Category.PVP, 120, 276); }
        protected String text(MinecraftClient mc) {
            if (mc.targetedEntity == null) return "Target: -";
            return "Target: " + mc.targetedEntity.getName().getString();
        }
    }

    public static class Velocity extends TextHud {
        public Velocity() { super("Velocity", "Shows the current three-axis velocity.", Category.PVP, 120, 296); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null) return "Velocity: -";
            var v = mc.player.getVelocity();
            return String.format(Locale.ROOT, "Velocity: %.2f %.2f %.2f", v.x, v.y, v.z);
        }
    }

    public static class OnGround extends TextHud {
        public OnGround() { super("On Ground", "Shows whether the player is grounded.", Category.PVP, 120, 316); }
        protected String text(MinecraftClient mc) { return "On Ground: " + (mc.player != null && mc.player.isOnGround()); }
    }

    public static class CombatTimer extends TextHud {
        private long lastAttack = -1;
        public CombatTimer() { super("Combat Timer", "Shows time since your last attack.", Category.PVP, 120, 336); }
        public void attacked() { lastAttack = System.currentTimeMillis(); }
        protected String text(MinecraftClient mc) {
            if (lastAttack < 0) return "Combat: -";
            return "Combat: " + (System.currentTimeMillis() - lastAttack) + " ms";
        }
    }

    public static class Crosshair extends HudModule {
        public Crosshair() {
            super("Custom Crosshair", "Draws a minimal client-side crosshair.", Category.VISUAL, 0, 0);
            setBackgroundEnabled(false);
        }
        @Override public void draw(DrawContext ctx, MinecraftClient mc) {
            int cx = mc.getWindow().getScaledWidth() / 2;
            int cy = mc.getWindow().getScaledHeight() / 2;
            ctx.fill(cx - 1, cy - 5, cx + 2, cy - 1, Theme.color(0f));
            ctx.fill(cx - 1, cy + 2, cx + 2, cy + 6, Theme.color(0f));
            ctx.fill(cx - 5, cy - 1, cx - 1, cy + 2, Theme.color(0f));
            ctx.fill(cx + 2, cy - 1, cx + 6, cy + 2, Theme.color(0f));
            width = 12; height = 12; x = cx - 6; y = cy - 6;
        }
    }

    public static class FpsMode extends TextHud {
        public FpsMode() { super("FPS Mode", "Shows FPS with a simple performance state.", Category.PERFORMANCE, 250, 256); }
        protected String text(MinecraftClient mc) {
            int fps = mc.getCurrentFps();
            String state = fps >= 120 ? "Excellent" : fps >= 60 ? "Good" : fps >= 30 ? "Low" : "Critical";
            return "FPS: " + fps + " | " + state;
        }
    }

    public static class CoordinatesNether extends TextHud {
        public CoordinatesNether() { super("Nether Coordinates", "Shows estimated Nether/Overworld coordinate conversion.", Category.PVE, 250, 276); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null || mc.world == null) return "Nether XYZ: -";
            int x = mc.player.getBlockX(), z = mc.player.getBlockZ();
            boolean nether = mc.world.getRegistryKey() == net.minecraft.world.World.NETHER;
            return nether
                    ? "Overworld: " + (x * 8) + " " + (mc.player.getBlockY()) + " " + (z * 8)
                    : "Nether: " + Math.floorDiv(x, 8) + " " + mc.player.getBlockY() + " " + Math.floorDiv(z, 8);
        }
    }

    public static class FoodHeld extends TextHud {
        public FoodHeld() { super("Food Held", "Shows hunger restored by the held food item.", Category.PVE, 250, 296); }
        protected String text(MinecraftClient mc) {
            if (mc.player == null || mc.player.getMainHandStack().isEmpty()) return "Food: -";
            var s = mc.player.getMainHandStack();
            return s.isFood() ? "Food: " + s.getName().getString() : "Food: -";
        }
    }

}
