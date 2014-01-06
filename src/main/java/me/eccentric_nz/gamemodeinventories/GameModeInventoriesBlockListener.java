/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesBlockListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesBlockListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                plugin.getBlock().removeBlock(event.getBlock().getLocation().toString());
            } else {
                event.setCancelled(true);
                String message = "You cannot break blocks that were placed in creative gamemode!";
                if (plugin.getConfig().getBoolean("track_creative_place.break_no_drop")) {
                    event.getBlock().setType(Material.AIR);
                    message = "Blocks that were placed in creative gamemode, do not give drops!";
                }
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    event.getPlayer().sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + message);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockplace(BlockPlaceEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            if (!plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
                plugin.getBlock().addBlock(event.getBlock().getLocation().toString());
            }
        }
    }
}
