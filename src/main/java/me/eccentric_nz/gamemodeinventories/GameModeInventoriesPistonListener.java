/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    List<BlockFace> faces = new ArrayList<BlockFace>();

    public GameModeInventoriesPistonListener(GameModeInventories plugin) {
        this.plugin = plugin;
        faces.add(BlockFace.UP);
        faces.add(BlockFace.DOWN);
        faces.add(BlockFace.NORTH);
        faces.add(BlockFace.SOUTH);
        faces.add(BlockFace.EAST);
        faces.add(BlockFace.WEST);

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
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
            return;
        }
        plugin.debug("getDirection() returns: " + event.getDirection().toString());
        for (BlockFace f : faces) {
            plugin.debug("block at block face: " + f.toString() + " is " + event.getBlock().getRelative(f).getType().toString());
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getRelative(event.getDirection(), 2).getLocation().toString())) {
            event.setCancelled(true);
            plugin.debug("Cancelled piston retraction becausethe moved blocks was a CREATIVE placed block");
        }
    }
}
