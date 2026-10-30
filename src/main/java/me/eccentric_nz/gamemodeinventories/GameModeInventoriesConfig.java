/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesConfig {

    // Append a new batch for each container version; never change an already released batch.
    private static final List<List<String>> CONTAINER_UPDATES = List.of(
            // Container config version 1: defaults updated for Minecraft 26.3.
            List.of("CHISELED_BOOKSHELF", "CRAFTER", "COPPER_CHEST", "EXPOSED_COPPER_CHEST", "WEATHERED_COPPER_CHEST", "OXIDIZED_COPPER_CHEST", "WAXED_COPPER_CHEST", "WAXED_EXPOSED_COPPER_CHEST", "WAXED_WEATHERED_COPPER_CHEST", "WAXED_OXIDIZED_COPPER_CHEST", "ACACIA_SHELF", "BAMBOO_SHELF", "BIRCH_SHELF", "CHERRY_SHELF", "CRIMSON_SHELF", "DARK_OAK_SHELF", "JUNGLE_SHELF", "MANGROVE_SHELF", "OAK_SHELF", "PALE_OAK_SHELF", "POPLAR_SHELF", "SPRUCE_SHELF", "WARPED_SHELF")
    );
    private static final int CONTAINERS_VERSION = CONTAINER_UPDATES.size();

    private final GameModeInventories plugin;
    private final FileConfiguration config;
    private final File configFile;
    HashMap<String, String> strOptions = new HashMap<>();
    HashMap<String, Integer> intOptions = new HashMap<>();
    HashMap<String, Boolean> boolOptions = new HashMap<>();
    List<String> containers = new ArrayList<>();
    List<String> bl = new ArrayList<>();
    List<String> com = new ArrayList<>();
    List<String> wor = new ArrayList<>();
    List<String> no = new ArrayList<>();

    public GameModeInventoriesConfig(GameModeInventories plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        // string
        strOptions.put("debug_level", "ERROR");
        strOptions.put("storage.mysql.server", "localhost");
        strOptions.put("storage.mysql.port", "3306");
        strOptions.put("storage.mysql.database", "GMI");
        strOptions.put("storage.mysql.user", "bukkit");
        strOptions.put("storage.mysql.password", "mysecurepassword");
        strOptions.put("storage.database", "sqlite");
        strOptions.put("storage.prefix", "");
        strOptions.put("creative_world.world", "creative");
        strOptions.put("creative_world.location", "last_known");
        // int
        intOptions.put("storage.mysql.pool_size", 10);
        // boolean
        boolOptions.put("armor", true);
        boolOptions.put("allow_creative_container_break", false);
        boolOptions.put("auto_update_containers", true);
        boolOptions.put("break_bedrock", false);
        boolOptions.put("bypass.inventories", true);
        boolOptions.put("bypass.container-copy", false);
        boolOptions.put("bypass.container-place", false);
        boolOptions.put("bypass.container-break", false);
        boolOptions.put("bypass.items", true);
        boolOptions.put("bypass.blacklist", false);
        boolOptions.put("bypass.commands", false);
        boolOptions.put("bypass.survival", false);
        boolOptions.put("bypass.trades", false);
        boolOptions.put("command_blacklist", false);
        boolOptions.put("creative_blacklist", false);
        boolOptions.put("debug", false);
        boolOptions.put("dont_spam_chat", false);
        boolOptions.put("enderchest", true);
        boolOptions.put("no_creative_pvp", false);
        boolOptions.put("no_drops", false);
        boolOptions.put("no_falling_drops", false);
        boolOptions.put("no_pickups", false);
        boolOptions.put("no_villager_trade", false);
        boolOptions.put("no_wither_spawn", false);
        boolOptions.put("no_golem_spawn", false);
        boolOptions.put("remove_potions", true);
        boolOptions.put("restrict_creative", false);
        boolOptions.put("restrict_spectator", false);
        boolOptions.put("save_on_death", true);
        boolOptions.put("creative_world.switch_to", false);
        boolOptions.put("track_creative_place.break_no_drop", false);
        boolOptions.put("track_creative_place.enabled", true);
        boolOptions.put("track_creative_place.no_piston_move", false);
        boolOptions.put("track_creative_place.attached_block", false);
        boolOptions.put("track_creative_place.no_seeds_from_pumpkin", false);
        boolOptions.put("track_creative_place.dont_track_is_whitelist", false);
        boolOptions.put("xp", true);
        boolOptions.put("uuid_conversion_done", false);
        boolOptions.put("blocks_conversion_done", false);
        boolOptions.put("xp_conversion_done", false);
        boolOptions.put("storage.mysql.test_connection", false);
        boolOptions.put("storage.mysql.useSSL", true);
        containers.add("ACACIA_SHELF");
        containers.add("ANVIL");
        containers.add("BAMBOO_SHELF");
        containers.add("BARREL");
        containers.add("BEACON");
        containers.add("BEE_NEST");
        containers.add("BEEHIVE");
        containers.add("BIRCH_SHELF");
        containers.add("BLACK_SHULKER_BOX");
        containers.add("BLAST_FURNACE");
        containers.add("BLUE_SHULKER_BOX");
        containers.add("BREWING_STAND");
        containers.add("BROWN_SHULKER_BOX");
        containers.add("CAMPFIRE");
        containers.add("CARTOGRAPHY_TABLE");
        containers.add("CHERRY_SHELF");
        containers.add("CHEST");
        containers.add("CHIPPED_ANVIL");
        containers.add("CHISELED_BOOKSHELF");
        containers.add("COMPOSTER");
        containers.add("COPPER_CHEST");
        containers.add("CRAFTER");
        containers.add("CRIMSON_SHELF");
        containers.add("CYAN_SHULKER_BOX");
        containers.add("DAMAGED_ANVIL");
        containers.add("DARK_OAK_SHELF");
        containers.add("DECORATED_POT");
        containers.add("DISPENSER");
        containers.add("DROPPER");
        containers.add("ENCHANTING_TABLE");
        containers.add("ENDER_CHEST");
        containers.add("EXPOSED_COPPER_CHEST");
        containers.add("FLETCHING_TABLE");
        containers.add("FURNACE");
        containers.add("GRAY_SHULKER_BOX");
        containers.add("GREEN_SHULKER_BOX");
        containers.add("GRINDSTONE");
        containers.add("HOPPER");
        containers.add("JUKEBOX");
        containers.add("JUNGLE_SHELF");
        containers.add("LECTERN");
        containers.add("LIGHT_BLUE_SHULKER_BOX");
        containers.add("LIGHT_GRAY_SHULKER_BOX");
        containers.add("LIME_SHULKER_BOX");
        containers.add("LOOM");
        containers.add("MAGENTA_SHULKER_BOX");
        containers.add("MANGROVE_SHELF");
        containers.add("OAK_SHELF");
        containers.add("ORANGE_SHULKER_BOX");
        containers.add("OXIDIZED_COPPER_CHEST");
        containers.add("PALE_OAK_SHELF");
        containers.add("PINK_SHULKER_BOX");
        containers.add("POPLAR_SHELF");
        containers.add("PURPLE_SHULKER_BOX");
        containers.add("RED_SHULKER_BOX");
        containers.add("SHULKER_BOX");
        containers.add("SMITHING_TABLE");
        containers.add("SMOKER");
        containers.add("SOUL_CAMPFIRE");
        containers.add("SPRUCE_SHELF");
        containers.add("STONECUTTER");
        containers.add("TRAPPED_CHEST");
        containers.add("WARPED_SHELF");
        containers.add("WAXED_COPPER_CHEST");
        containers.add("WAXED_EXPOSED_COPPER_CHEST");
        containers.add("WAXED_OXIDIZED_COPPER_CHEST");
        containers.add("WAXED_WEATHERED_COPPER_CHEST");
        containers.add("WEATHERED_COPPER_CHEST");
        containers.add("WHITE_SHULKER_BOX");
        containers.add("YELLOW_SHULKER_BOX");
        bl.add("TNT");
        bl.add("BEDROCK");
        bl.add("LAVA_BUCKET");
        com.add("give");
        com.add("i");
        com.add("buy");
        com.add("sell");
        wor.add("world");
        no.add("STONE");
        no.add("DIRT");
    }

    public void checkConfig() {
        int i = 0;
        // string values
        for (Map.Entry<String, String> entry : strOptions.entrySet()) {
            if (!config.contains(entry.getKey())) {
                plugin.getConfig().set(entry.getKey(), entry.getValue());
                i++;
            }
        }
        // int values
        for (Map.Entry<String, Integer> entry : intOptions.entrySet()) {
            if (!config.contains(entry.getKey())) {
                plugin.getConfig().set(entry.getKey(), entry.getValue());
                i++;
            }
        }
        // boolean values
        for (Map.Entry<String, Boolean> entry : boolOptions.entrySet()) {
            if (!config.contains(entry.getKey())) {
                if (entry.getKey().equals("track_creative_place.enabled")) {
                    // check for previous enrty
                    if (plugin.getConfig().contains("track_creative_place")) {
                        plugin.getConfig().set(entry.getKey(), plugin.getConfig().getBoolean("track_creative_place"));
                    } else {
                        plugin.getConfig().set(entry.getKey(), entry.getValue());
                    }
                }
                plugin.getConfig().set(entry.getKey(), entry.getValue());
                i++;
            }
        }
        if (!config.contains("containers")) {
            plugin.getConfig().set("containers", containers);
            plugin.getConfig().set("containers_version", CONTAINERS_VERSION);
            i++;
        } else {
            int version = config.getInt("containers_version", 0);
            if (!config.contains("containers_version")) {
                plugin.getConfig().set("containers_version", version);
            }
            if (version < CONTAINERS_VERSION) {
                if (version > 0 && plugin.getConfig().getBoolean("auto_update_containers", true)) {
                    i += migrateContainers();
                } else {
                    updateContainerSuggestions(version);
                    plugin.getLogger().info("GameModeInventories has an updated container list. "
                            + "Review the commented suggestions in config.yml, or run "
                            + "'gmi auto-update-container-config' to include all default containers and enable updates, or "
                            + "'gmi auto-update-container-config false' to keep your list and disable automatic updates.");
                }
            }
        }
        if (!config.contains("blacklist")) {
            plugin.getConfig().set("blacklist", bl);
            i++;
        } else if (plugin.getConfig().getStringList("blacklist").contains("ZOMBIE_PIGMAN_SPAWN_EGG")) {
            List<String> black = plugin.getConfig().getStringList("blacklist");
            black.remove("ZOMBIE_PIGMAN_SPAWN_EGG");
            black.add("ZOMBIFIED_PIGLIN_SPAWN_EGG");
            plugin.getConfig().set("blacklist", black);
            i++;
        }
        if (!config.contains("commands")) {
            plugin.getConfig().set("commands", com);
            i++;
        }
        if (!config.contains("track_creative_place.worlds")) {
            plugin.getConfig().set("track_creative_place.worlds", wor);
            i++;
        }
        if (!config.contains("track_creative_place.dont_track")) {
            plugin.getConfig().set("track_creative_place.dont_track", no);
            i++;
        }
        if (config.contains("storage.mysql.url")) {
            String url = config.getString("storage.mysql.url");
            // mysql://localhost:3306/GMI
            String[] split = url.split("/");
            String[] sp = split[2].split(":");
            plugin.getConfig().set("storage.mysql.server", sp[0]);
            plugin.getConfig().set("storage.mysql.port", sp[1]);
            plugin.getConfig().set("storage.mysql.database", split[3]);
            plugin.getConfig().set("storage.mysql.url", null);
        }
        if (i > 0) {
            plugin.getLogger().log(Level.INFO, "Added " + i + " new items to config");
        }
        plugin.saveConfig();
    }

    /** Applies pending container defaults without replacing custom entries. */
    public int migrateContainers() {
        return migrateContainers(false);
    }

    private int migrateContainers(boolean includeAll) {
        // Read the saved version explicitly: packaged defaults may already contain the latest one.
        int version = config.getInt("containers_version", 0);
        if (!includeAll && version >= CONTAINERS_VERSION) {
            return 0;
        }
        List<String> list = plugin.getConfig().getStringList("containers");
        List<String> added = new ArrayList<>();
        for (String container : includeAll ? containers : pendingContainers(version)) {
            if (!list.contains(container)) {
                list.add(container);
                added.add(container);
            }
        }
        plugin.getConfig().set("containers", list);
        plugin.getConfig().set("containers_version", Math.max(version, CONTAINERS_VERSION));
        clearContainerSuggestions();
        plugin.saveConfig();
        if (!added.isEmpty()) {
            plugin.getLogger().info("Added containers to config: " + String.join(", ", added));
        }
        return added.size();
    }

    /** Explicit opt-in includes all defaults, even if an earlier update was declined. */
    public int setAutoUpdateContainers(boolean enabled) {
        if (enabled) {
            plugin.getConfig().set("auto_update_containers", true);
            return migrateContainers(true);
        }
        declineContainerMigration();
        return 0;
    }

    /** Marks the current defaults as reviewed without changing the active list. */
    private void declineContainerMigration() {
        int version = config.getInt("containers_version", 0);
        plugin.getConfig().set("containers_version", Math.max(version, CONTAINERS_VERSION));
        plugin.getConfig().set("auto_update_containers", false);
        clearContainerSuggestions();
        plugin.saveConfig();
    }

    private List<String> pendingContainers(int version) {
        if (version <= 0) {
            return containers;
        }
        List<String> pending = new ArrayList<>();
        for (int next = version; next < CONTAINERS_VERSION; next++) {
            pending.addAll(CONTAINER_UPDATES.get(next));
        }
        return pending;
    }

    private void updateContainerSuggestions(int version) {
        clearContainerSuggestions();
        List<String> comments = new ArrayList<>(plugin.getConfig().getComments("containers"));
        comments.add("GMI pending container suggestions (start)");
        comments.add("Existing entries below remain active. To include all defaults and enable updates, run:");
        comments.add("/gmi auto-update-container-config");
        comments.add("To keep your list and disable automatic updates: /gmi auto-update-container-config false");
        comments.add("Or copy selected entries into the containers list and restart the server.");
        List<String> current = plugin.getConfig().getStringList("containers");
        for (String container : pendingContainers(version)) {
            if (!current.contains(container)) {
                comments.add("- " + container);
            }
        }
        comments.add("GMI pending container suggestions (end)");
        plugin.getConfig().setComments("containers", comments);
    }

    private void clearContainerSuggestions() {
        List<String> comments = new ArrayList<>();
        boolean suggestion = false;
        for (String comment : plugin.getConfig().getComments("containers")) {
            if ("GMI pending container suggestions (start)".equals(comment)) {
                suggestion = true;
            } else if ("GMI pending container suggestions (end)".equals(comment)) {
                suggestion = false;
            } else if (!suggestion) {
                comments.add(comment);
            }
        }
        plugin.getConfig().setComments("containers", comments);
    }

}
