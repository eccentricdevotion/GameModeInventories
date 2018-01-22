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
        wouldDrop.add(Material.ACACIA_DOOR);
        wouldDrop.add(Material.ACACIA_PRESSURE_PLATE);
        wouldDrop.add(Material.ACACIA_SAPLING);
        wouldDrop.add(Material.ACACIA_TRAPDOOR);
        wouldDrop.add(Material.ALLIUM);
        wouldDrop.add(Material.AZURE_BLUET);
        wouldDrop.add(Material.BIRCH_DOOR);
        wouldDrop.add(Material.BIRCH_PRESSURE_PLATE);
        wouldDrop.add(Material.BIRCH_SAPLING);
        wouldDrop.add(Material.BIRCH_TRAPDOOR);
        wouldDrop.add(Material.BLACK_BED);
        wouldDrop.add(Material.BLUE_BED);
        wouldDrop.add(Material.BLUE_ORCHID);
        wouldDrop.add(Material.BROWN_BED);
        wouldDrop.add(Material.BROWN_MUSHROOM);
        wouldDrop.add(Material.COBWEB);
        wouldDrop.add(Material.CYAN_BED);
        wouldDrop.add(Material.DANDELION);
        wouldDrop.add(Material.DARK_OAK_DOOR);
        wouldDrop.add(Material.DARK_OAK_PRESSURE_PLATE);
        wouldDrop.add(Material.DARK_OAK_SAPLING);
        wouldDrop.add(Material.DARK_OAK_TRAPDOOR);
        wouldDrop.add(Material.DEAD_BUSH);
        wouldDrop.add(Material.FERN);
        wouldDrop.add(Material.GRASS);
        wouldDrop.add(Material.GRAY_BED);
        wouldDrop.add(Material.GREEN_BED);
        wouldDrop.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        wouldDrop.add(Material.IRON_DOOR); // Still Breaks
        wouldDrop.add(Material.IRON_TRAPDOOR);
        wouldDrop.add(Material.JACK_O_LANTERN);
        wouldDrop.add(Material.JUNGLE_DOOR);
        wouldDrop.add(Material.JUNGLE_PRESSURE_PLATE);
        wouldDrop.add(Material.JUNGLE_SAPLING);
        wouldDrop.add(Material.JUNGLE_TRAPDOOR);
        wouldDrop.add(Material.LADDER);
        wouldDrop.add(Material.LARGE_FERN);
        wouldDrop.add(Material.LEVER);
        wouldDrop.add(Material.LIGHT_BLUE_BED);
        wouldDrop.add(Material.LIGHT_GRAY_BED);
        wouldDrop.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        wouldDrop.add(Material.LILAC);
        wouldDrop.add(Material.LILY_PAD);
        wouldDrop.add(Material.LIME_BED);
        wouldDrop.add(Material.MAGENTA_BED);
        wouldDrop.add(Material.MELON_BLOCK);
        wouldDrop.add(Material.OAK_DOOR); // Still Breaks
        wouldDrop.add(Material.OAK_PRESSURE_PLATE);
        wouldDrop.add(Material.OAK_SAPLING);
        wouldDrop.add(Material.OAK_TRAPDOOR);
        wouldDrop.add(Material.ORANGE_BED);
        wouldDrop.add(Material.ORANGE_TULIP);
        wouldDrop.add(Material.OXEYE_DAISY);
        wouldDrop.add(Material.PEONY);
        wouldDrop.add(Material.PINK_BED);
        wouldDrop.add(Material.PINK_TULIP);
        wouldDrop.add(Material.POPPY);
        wouldDrop.add(Material.PUMPKIN);
        wouldDrop.add(Material.PURPLE_BED);
        wouldDrop.add(Material.REDSTONE);
        wouldDrop.add(Material.REDSTONE_TORCH);
        wouldDrop.add(Material.REDSTONE_WALL_TORCH);
        wouldDrop.add(Material.REDSTONE_WIRE);
        wouldDrop.add(Material.RED_BED);
        wouldDrop.add(Material.RED_MUSHROOM);
        wouldDrop.add(Material.RED_TULIP);
        wouldDrop.add(Material.REPEATER);
        wouldDrop.add(Material.ROSE_BUSH);
        wouldDrop.add(Material.SPRUCE_DOOR);
        wouldDrop.add(Material.SPRUCE_PRESSURE_PLATE);
        wouldDrop.add(Material.SPRUCE_SAPLING);
        wouldDrop.add(Material.SPRUCE_TRAPDOOR);
        wouldDrop.add(Material.STONE_BUTTON);
        wouldDrop.add(Material.STONE_PRESSURE_PLATE);
        wouldDrop.add(Material.SUNFLOWER);
        wouldDrop.add(Material.TALL_GRASS);
        wouldDrop.add(Material.TORCH);
        wouldDrop.add(Material.TRIPWIRE_HOOK);
        wouldDrop.add(Material.WHITE_BED); // Still Breaks
        wouldDrop.add(Material.WHITE_TULIP);
        wouldDrop.add(Material.YELLOW_BED);
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
