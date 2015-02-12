/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesCommandListener implements Listener {

    private final GameModeInventories plugin;
    private final List<String> blacklist;

    public GameModeInventoriesCommandListener(GameModeInventories plugin) {
        this.plugin = plugin;
        this.blacklist = plugin.getConfig().getStringList("commands");
        for (int i = 0, l = this.blacklist.size(); i < l; ++i) {
            this.blacklist.set(i, this.blacklist.get(i).toLowerCase());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || !plugin.getConfig().getBoolean("command_blacklist") || GameModeInventoriesBypass.canBypass(event.getPlayer(), "commands", plugin)) {
            return;
        }
        String message = event.getMessage();
        // get the command from the message
        String[] args = message.split(" ");
        if (args.length > 0) {
            String command = args[0].substring(1).toLowerCase();
            if (blacklist.contains(command)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    event.getPlayer().sendMessage(plugin.MY_PLUGIN_NAME + String.format(plugin.getM().getMessage().get("NO_CREATIVE_COMMAND"), ChatColor.GREEN + "/" + command + ChatColor.RESET));
                }
            }
        }
    }
}
