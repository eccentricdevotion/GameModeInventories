/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesMessage {

    private final GameModeInventories plugin;
    private final HashMap<String, String> message = new HashMap<>();
    private final FileConfiguration messagesConfig;
    private final File messagesFile;
    HashMap<String, String> messageOptions = new HashMap<>();

    public GameModeInventoriesMessage(GameModeInventories plugin) {
        this.plugin = plugin;
        messagesFile = getMessagesFile();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messageOptions.put("CONFIG_SET", "%s was set to: %s");
        messageOptions.put("HELP", "There is no help! Just switch game modes, and your inventory/armor/xp will change.");
        messageOptions.put("INVALID_MATERIAL", "Invalid material in blacklist");
        messageOptions.put("INVALID_MATERIAL_TRACK", "Invalid material in dont_track list");
        messageOptions.put("NO_CREATIVE_BREAK", "You cannot break blocks that were placed in CREATIVE gamemode!");
        messageOptions.put("NO_CREATIVE_COMMAND", "You are not allowed to use %s in CREATIVE!");
        messageOptions.put("NO_CREATIVE_DROPS", "Blocks that were placed in CREATIVE gamemode, do not give drops!");
        messageOptions.put("NO_CREATIVE_HORSE", "You are not allowed to access horse inventories in CREATIVE!");
        messageOptions.put("NO_CREATIVE_INVENTORY", "You are not allowed to access inventories in CREATIVE!");
        messageOptions.put("NO_CREATIVE_PICKUP", "You are not allowed to pick up items in CREATIVE!");
        messageOptions.put("NO_CREATIVE_PLACE", "%s placement is disabled in CREATIVE gamemode!");
        messageOptions.put("NO_PERMISSION", "You do not have permission to run that command!");
        messageOptions.put("NO_PLAYER_DROPS", "You are not allowed to drop items in CREATIVE!");
        messageOptions.put("NO_WORKBENCH_DROPS", "Workbenches do not drop items in CREATIVE!");
        messageOptions.put("NO_SPECTATOR", "You are not allowed to be a SPECTATOR!");
        messageOptions.put("NO_TRADE", "You are not allowed to trade with villagers in CREATIVE!");
    }

    public void getMessages() {
        messagesConfig.getKeys(false).forEach((m) -> {
            message.put(m, messagesConfig.getString(m));
        });
    }

    public HashMap<String, String> getMessage() {
        return message;
    }

    private File getMessagesFile() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        InputStream in = plugin.getResource("messages.yml");
        if (!file.exists()) {
            OutputStream out;
            try {
                out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                try {
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } catch (IOException io) {
                    plugin.getLogger().log(Level.WARNING, "[GameModeInventories] Could not save the file (" + file.toString() + ").");
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            } catch (FileNotFoundException e) {
                plugin.getLogger().log(Level.WARNING, "[GameModeInventories] File not found.");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return file;
    }

    public void updateMessages() {
        // message values
        int m = 0;
        for (Map.Entry<String, String> entry : messageOptions.entrySet()) {
            if (!messagesConfig.contains(entry.getKey())) {
                plugin.getConfig().set(entry.getKey(), entry.getValue());
                m++;
            }
        }
        if (m > 0) {
            try {
                messagesConfig.save(messagesFile);
                plugin.getLogger().log(Level.INFO, "Added " + m + " new items to messages.yml");
            } catch (IOException ex) {
                plugin.debug("Could not save messages.yml, " + ex.getMessage());
            }
        }
    }
}
