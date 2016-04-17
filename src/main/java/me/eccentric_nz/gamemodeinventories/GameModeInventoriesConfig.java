/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesConfig {

    private final GameModeInventories plugin;
    private FileConfiguration config = null;
    private File configFile = null;
//    private FileConfiguration messages = null;
//    private File messagesFile = null;
    HashMap<String, String> strOptions = new HashMap<String, String>();
    HashMap<String, Integer> intOptions = new HashMap<String, Integer>();
    HashMap<String, Boolean> boolOptions = new HashMap<String, Boolean>();
    List<String> bl = new ArrayList<String>();
    List<String> com = new ArrayList<String>();
    List<String> wor = new ArrayList<String>();
    List<String> no = new ArrayList<String>();

    public GameModeInventoriesConfig(GameModeInventories plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
//        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
//        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
        // string
        strOptions.put("debug_level", "ERROR");
        strOptions.put("storage.mysql.server", "localhost");
        strOptions.put("storage.mysql.port", "3306");
        strOptions.put("storage.mysql.database", "GMI");
        strOptions.put("storage.mysql.user", "bukkit");
        strOptions.put("storage.mysql.password", "mysecurepassword");
        strOptions.put("storage.database", "sqlite");
        strOptions.put("creative_world.world", "creative");
        strOptions.put("creative_world.location", "last_known");
        // int
        intOptions.put("storage.mysql.pool_size", 10);
        // boolean
        boolOptions.put("armor", true);
        boolOptions.put("break_bedrock", false);
        boolOptions.put("bypass.inventories", true);
        boolOptions.put("bypass.items", true);
        boolOptions.put("bypass.blacklist", false);
        boolOptions.put("bypass.commands", false);
        boolOptions.put("bypass.survival", false);
        boolOptions.put("command_blacklist", false);
        boolOptions.put("creative_blacklist", false);
        boolOptions.put("custom_attributes", false);
        boolOptions.put("debug", false);
        boolOptions.put("dont_spam_chat", false);
        boolOptions.put("enderchest", true);
        boolOptions.put("no_drops", false);
        boolOptions.put("no_falling_drops", false);
        boolOptions.put("no_pickups", false);
        boolOptions.put("remove_potions", true);
        boolOptions.put("restrict_creative", false);
        boolOptions.put("restrict_spectator", false);
        boolOptions.put("save_on_death", true);
        boolOptions.put("creative_world.switch_to", false);
        boolOptions.put("track_creative_place.break_no_drop", false);
        boolOptions.put("track_creative_place.enabled", true);
        boolOptions.put("track_creative_place.no_piston_move", false);
        boolOptions.put("track_creative_place.attached_block", false);
        boolOptions.put("track_creative_place.dont_track_is_whitelist", false);
        boolOptions.put("xp", true);
        boolOptions.put("uuid_conversion_done", false);
        boolOptions.put("blocks_conversion_done", false);
        boolOptions.put("storage.mysql.test_connection", false);
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
        if (!config.contains("blacklist")) {
            plugin.getConfig().set("blacklist", bl);
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
            plugin.getServer().getConsoleSender().sendMessage(plugin.MY_PLUGIN_NAME + "Added " + ChatColor.AQUA + i + ChatColor.RESET + " new items to config");
        }
        plugin.saveConfig();
    }
}
