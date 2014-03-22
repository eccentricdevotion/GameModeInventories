/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesPistonListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesPistonListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
            return;
        }
        for (Block b : event.getBlocks()) {
            if (plugin.getCreativeBlocks().contains(b.getLocation().toString())) {
                event.setCancelled(true);
                plugin.debug("Cancelled piston extension because one of the moved blocks was a CREATIVE placed block");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getRelative(event.getDirection(), 2).getLocation().toString())) {
            event.setCancelled(true);
            plugin.debug("Cancelled piston retraction because the moved block was a CREATIVE placed block");
        }
    }
}
