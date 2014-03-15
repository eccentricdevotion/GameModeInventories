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
    HashMap<String, Boolean> boolOptions = new HashMap<String, Boolean>();
    List<String> bl = new ArrayList<String>();

    public GameModeInventoriesConfig(GameModeInventories plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        // boolean
        boolOptions.put("armor", true);
        boolOptions.put("creative_blacklist", false);
        boolOptions.put("debug", false);
        boolOptions.put("dont_spam_chat", false);
        boolOptions.put("enderchest", true);
        boolOptions.put("no_drops", false);
        boolOptions.put("no_falling_drops", false);
        boolOptions.put("no_pickups", false);
        boolOptions.put("remove_potions", true);
        boolOptions.put("restrict_creative", false);
        boolOptions.put("save_on_death", true);
        boolOptions.put("switch_on_reload", false);
        boolOptions.put("xp", true);
        boolOptions.put("track_creative_place.enabled", true);
        boolOptions.put("track_creative_place.break_no_drop", false);
        bl.add("TNT");
        bl.add("BEDROCK");
        bl.add("LAVA_BUCKET");
    }

    public void checkConfig() {
        int i = 0;
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
        }
        if (i > 0) {
            plugin.getServer().getConsoleSender().sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + "Added " + ChatColor.AQUA + i + ChatColor.RESET + " new items to config");
        }
        plugin.saveConfig();
    }
}
