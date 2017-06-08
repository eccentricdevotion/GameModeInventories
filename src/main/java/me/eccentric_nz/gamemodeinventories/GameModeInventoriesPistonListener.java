/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
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
    private final List<Material> wouldDrop = new ArrayList<>();

    public GameModeInventoriesPistonListener(GameModeInventories plugin) {
        this.plugin = plugin;
        wouldDrop.add(Material.BED_BLOCK); // Still Breaks
        wouldDrop.add(Material.BROWN_MUSHROOM);
        wouldDrop.add(Material.DIODE_BLOCK_OFF);
        wouldDrop.add(Material.DIODE_BLOCK_ON);
        wouldDrop.add(Material.GOLD_PLATE);
        wouldDrop.add(Material.IRON_DOOR_BLOCK); // Still Breaks
        wouldDrop.add(Material.IRON_PLATE);
        wouldDrop.add(Material.JACK_O_LANTERN);
        wouldDrop.add(Material.LADDER);
        wouldDrop.add(Material.LEVER);
        wouldDrop.add(Material.MELON_BLOCK);
        wouldDrop.add(Material.PUMPKIN);
        wouldDrop.add(Material.REDSTONE);
        wouldDrop.add(Material.REDSTONE_TORCH_OFF);
        wouldDrop.add(Material.REDSTONE_TORCH_ON);
        wouldDrop.add(Material.REDSTONE_WIRE);
        wouldDrop.add(Material.RED_MUSHROOM);
        wouldDrop.add(Material.RED_ROSE);
        wouldDrop.add(Material.SAPLING);
        wouldDrop.add(Material.STONE_BUTTON);
        wouldDrop.add(Material.STONE_PLATE);
        wouldDrop.add(Material.TORCH);
        wouldDrop.add(Material.TRAP_DOOR);
        wouldDrop.add(Material.TRIPWIRE_HOOK);
        wouldDrop.add(Material.WATER_LILY);
        wouldDrop.add(Material.WEB);
        wouldDrop.add(Material.WOODEN_DOOR); // Still Breaks
        wouldDrop.add(Material.WOOD_PLATE);
        wouldDrop.add(Material.YELLOW_FLOWER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (plugin.getConfig().getBoolean("track_creative_place.no_piston_move")) {
            Block block = event.getBlock();
            if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(block.getWorld().getName())) {
                return;
            }
            for (Block b : event.getBlocks()) {
                String gmiwc = block.getWorld().getName() + "," + block.getChunk().getX() + "," + block.getChunk().getZ();
                if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
                    return;
                }
                if (plugin.getCreativeBlocks().get(gmiwc).contains(b.getLocation().toString())) {
                    if (wouldDrop.contains(b.getType())) {
                        event.setCancelled(true);
                        plugin.debug("Cancelled piston extension because one of the moved blocks would drop an item");
                        return;
                    } else if (plugin.getCreativeBlocks().get(gmiwc).contains(block.getLocation().toString())) {
                        // update the location of the moved block
                        plugin.getCreativeBlocks().get(gmiwc).remove(b.getLocation().toString());
                        plugin.getCreativeBlocks().get(gmiwc).add(b.getRelative(event.getDirection()).getLocation().toString());
                    } else {
                        event.setCancelled(true);
                        plugin.debug("Cancelled piston extension because one of the moved blocks was a CREATIVE placed block");
                    }
                }
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
        Block block = event.getBlock();
        if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(block.getWorld().getName())) {
            return;
        }
        String gmiwc = block.getWorld().getName() + "," + block.getChunk().getX() + "," + block.getChunk().getZ();
        if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
            return;
        }
        if (plugin.getCreativeBlocks().get(gmiwc).contains(block.getRelative(event.getDirection(), 2).getLocation().toString())) {
            if (plugin.getCreativeBlocks().get(gmiwc).contains(block.getLocation().toString())) {
                // update the location of the moved block
                plugin.getCreativeBlocks().get(gmiwc).remove(block.getRelative(event.getDirection(), 2).getLocation().toString());
                plugin.getCreativeBlocks().get(gmiwc).add(block.getRelative(event.getDirection()).getLocation().toString());
            } else {
                event.setCancelled(true);
                plugin.debug("Cancelled piston retraction because the moved block was a CREATIVE placed block");
            }
        }
    }
}
