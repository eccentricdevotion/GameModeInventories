/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesMessage {

    private final GameModeInventories plugin;
    private FileConfiguration messagesConfig = null;
    private File messagesFile = null;
    private final HashMap<String, String> message = new HashMap<String, String>();

    public GameModeInventoriesMessage(GameModeInventories plugin) {
        this.plugin = plugin;
        this.messagesFile = getMessagesFile();
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void getMessages() {
        for (String m : messagesConfig.getKeys(false)) {
            message.put(m, messagesConfig.getString(m));
        }
    }

    public HashMap<String, String> getMessage() {
        return message;
    }

    private File getMessagesFile() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        InputStream in = plugin.getResource("messages.yml");
        if (!file.exists()) {
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                try {
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } catch (IOException io) {
                    System.err.println("[GameModeInventories] Could not save the file (" + file.toString() + ").");
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("[GameModeInventories] File not found.");
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
        int i = 0;
        if (!messagesConfig.contains("NO_SPECTATOR")) {
            messagesConfig.set("NO_SPECTATOR", "You are not allowed to be a SPECTATOR!");
            i++;
        }
        if (!messagesConfig.contains("INVALID_MATERIAL_TRACK")) {
            messagesConfig.set("INVALID_MATERIAL_TRACK", "Invalid material in dont_track list");
            i++;
        }
        if (i > 0) {
            try {
                messagesConfig.save(new File(plugin.getDataFolder(), "messages.yml"));
                plugin.getServer().getConsoleSender().sendMessage(plugin.MY_PLUGIN_NAME + "Added " + ChatColor.AQUA + i + ChatColor.RESET + " new items to messages.yml");
            } catch (IOException io) {
                plugin.debug("Could not save messages.yml, " + io);
            }
        }
    }
}
