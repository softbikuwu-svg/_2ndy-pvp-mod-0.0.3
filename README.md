# _2ndy Client

A lightweight Fabric client for Minecraft 1.21.11 focused on PvP, PvE, HUD customization and performance-friendly client-side information.

## Included
- 40+ lightweight modules across HUD, PvP, PvE, Utility, Visual and Performance categories.
- Windows 11-inspired black/dark UI.
- English-only UI and module descriptions.
- Draggable HUD: open chat (`T`) and drag enabled HUD modules.
- Per-HUD background toggle: in the client menu, right-click any HUD module to switch its background on/off.
- Persistent configuration in `.minecraft/config/secondy_client.json`.
- FPS, CPS, Keystrokes, Coordinates, Armor, Ping, Direction, Biome, Speed, Target Info, Target Health, Attack Cooldown, Item Counter, Effects, Mob Counter, Session Time, Clock, Memory, World Time, Armor Durability, Frame Time, Combo, Reach Display, Toggle Sprint, Zoom, Server Info, Fullbright and Chroma.
- No combat automation, packet manipulation, or server-side cheating features.

## Controls
- `Right Shift` — open/close the client menu.
- `T` — open chat, then drag HUD elements.
- `C` — hold to zoom while Zoom is enabled.
- Left click — toggle a module.
- Right click on a HUD module — toggle that module's background.

## Build
GitHub Actions builds the JAR automatically. Open **Actions → build → Artifacts → secondy-client-jar**.

Local build requires Java 21 and Gradle 9.2.1:
`gradle build`
