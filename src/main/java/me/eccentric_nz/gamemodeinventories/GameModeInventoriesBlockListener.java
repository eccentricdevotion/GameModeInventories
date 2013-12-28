/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
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
        if (!plugin.getConfig().getBoolean("track_creative_place")) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                plugin.getBlock().removeBlock(event.getBlock().getLocation().toString());
            } else {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    event.getPlayer().sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + "You cannot break blocks that were placed in creative gamemode!");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockplace(BlockPlaceEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place")) {
            return;
        }
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            if (!plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
                plugin.getBlock().addBlock(event.getBlock().getLocation().toString());
            }
        }
    }
}
