package com.secondy.client.util;

import com.google.gson.*;
import com.secondy.client.SecondyClient;
import com.secondy.client.module.HudModule;
import com.secondy.client.module.Module;
import com.secondy.client.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Config() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("secondy_client.json");
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            JsonObject root = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            for (Module m : ModuleManager.ALL) {
                if (!root.has(m.name) || !root.get(m.name).isJsonObject()) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (o.has("enabled")) m.setEnabled(o.get("enabled").getAsBoolean());
                if (m instanceof HudModule h) {
                    if (o.has("x")) h.x = o.get("x").getAsInt();
                    if (o.has("y")) h.y = o.get("y").getAsInt();
                    if (o.has("background")) h.setBackgroundEnabled(o.get("background").getAsBoolean());
                    if (o.has("backgroundOpacity")) h.setBackgroundOpacity(o.get("backgroundOpacity").getAsInt());
                }
            }
        } catch (Exception e) {
            SecondyClient.LOGGER.error("Could not load config", e);
        }
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ModuleManager.ALL) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                if (m instanceof HudModule h) {
                    o.addProperty("x", h.x);
                    o.addProperty("y", h.y);
                    o.addProperty("background", h.hasBackground());
                    o.addProperty("backgroundOpacity", h.getBackgroundOpacity());
                }
                root.add(m.name, o);
            }
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception e) {
            SecondyClient.LOGGER.error("Could not save config", e);
        }
    }
}
