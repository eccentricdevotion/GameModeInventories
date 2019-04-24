/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesPhysicsListener implements Listener {

    private final GameModeInventories plugin;
    private final List<Material> willdrop = new ArrayList<>();
    private final List<Material> doors = new ArrayList<>();
    private final List<Material> plates = new ArrayList<>();

    public GameModeInventoriesPhysicsListener(GameModeInventories plugin) {
        this.plugin = plugin;
        willdrop.add(Material.ACACIA_BUTTON);
        willdrop.add(Material.ACACIA_DOOR);
        willdrop.add(Material.ACACIA_PRESSURE_PLATE);
        willdrop.add(Material.ACACIA_SAPLING);
        willdrop.add(Material.ACACIA_TRAPDOOR);
        willdrop.add(Material.ACTIVATOR_RAIL);
        willdrop.add(Material.ALLIUM);
        willdrop.add(Material.AZURE_BLUET);
        willdrop.add(Material.BIRCH_BUTTON);
        willdrop.add(Material.BIRCH_DOOR);
        willdrop.add(Material.BIRCH_PRESSURE_PLATE);
        willdrop.add(Material.BIRCH_SAPLING);
        willdrop.add(Material.BIRCH_TRAPDOOR);
        willdrop.add(Material.BLACK_BANNER);
        willdrop.add(Material.BLACK_CARPET);
        willdrop.add(Material.BLACK_WALL_BANNER);
        willdrop.add(Material.BLUE_BANNER);
        willdrop.add(Material.BLUE_CARPET);
        willdrop.add(Material.BLUE_ORCHID);
        willdrop.add(Material.BLUE_WALL_BANNER);
        willdrop.add(Material.BROWN_BANNER);
        willdrop.add(Material.BROWN_CARPET);
        willdrop.add(Material.BROWN_MUSHROOM);
        willdrop.add(Material.BROWN_WALL_BANNER);
        willdrop.add(Material.COMPARATOR);
        willdrop.add(Material.CYAN_BANNER);
        willdrop.add(Material.CYAN_CARPET);
        willdrop.add(Material.CYAN_WALL_BANNER);
        willdrop.add(Material.DANDELION);
        willdrop.add(Material.DARK_OAK_BUTTON);
        willdrop.add(Material.DARK_OAK_DOOR);
        willdrop.add(Material.DARK_OAK_PRESSURE_PLATE);
        willdrop.add(Material.DARK_OAK_SAPLING);
        willdrop.add(Material.DARK_OAK_TRAPDOOR);
        willdrop.add(Material.DEAD_BUSH);
        willdrop.add(Material.DETECTOR_RAIL);
        willdrop.add(Material.FERN);
        willdrop.add(Material.FLOWER_POT);
        willdrop.add(Material.GRASS);
        willdrop.add(Material.GRAY_BANNER);
        willdrop.add(Material.GRAY_CARPET);
        willdrop.add(Material.GRAY_WALL_BANNER);
        willdrop.add(Material.GREEN_BANNER);
        willdrop.add(Material.GREEN_CARPET);
        willdrop.add(Material.GREEN_WALL_BANNER);
        willdrop.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        willdrop.add(Material.IRON_DOOR);
        willdrop.add(Material.IRON_TRAPDOOR);
        willdrop.add(Material.JUNGLE_BUTTON);
        willdrop.add(Material.JUNGLE_DOOR);
        willdrop.add(Material.JUNGLE_PRESSURE_PLATE);
        willdrop.add(Material.JUNGLE_SAPLING);
        willdrop.add(Material.JUNGLE_TRAPDOOR);
        willdrop.add(Material.LADDER);
        willdrop.add(Material.LARGE_FERN);
        willdrop.add(Material.LEVER);
        willdrop.add(Material.LIGHT_BLUE_BANNER);
        willdrop.add(Material.LIGHT_BLUE_CARPET);
        willdrop.add(Material.LIGHT_BLUE_WALL_BANNER);
        willdrop.add(Material.LIGHT_GRAY_BANNER);
        willdrop.add(Material.LIGHT_GRAY_CARPET);
        willdrop.add(Material.LIGHT_GRAY_WALL_BANNER);
        willdrop.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        willdrop.add(Material.LILAC);
        willdrop.add(Material.LIME_BANNER);
        willdrop.add(Material.LIME_CARPET);
        willdrop.add(Material.LIME_WALL_BANNER);
        willdrop.add(Material.MAGENTA_BANNER);
        willdrop.add(Material.MAGENTA_CARPET);
        willdrop.add(Material.MAGENTA_WALL_BANNER);
        willdrop.add(Material.NETHER_WART);
        willdrop.add(Material.OAK_BUTTON);
        willdrop.add(Material.OAK_PRESSURE_PLATE);
        willdrop.add(Material.OAK_SAPLING);
        willdrop.add(Material.OAK_TRAPDOOR);
        willdrop.add(Material.ORANGE_BANNER);
        willdrop.add(Material.ORANGE_CARPET);
        willdrop.add(Material.ORANGE_TULIP);
        willdrop.add(Material.ORANGE_WALL_BANNER);
        willdrop.add(Material.OXEYE_DAISY);
        willdrop.add(Material.PAINTING);
        willdrop.add(Material.PEONY);
        willdrop.add(Material.PINK_BANNER);
        willdrop.add(Material.PINK_CARPET);
        willdrop.add(Material.PINK_TULIP);
        willdrop.add(Material.PINK_WALL_BANNER);
        willdrop.add(Material.POPPY);
        willdrop.add(Material.PURPLE_BANNER);
        willdrop.add(Material.PURPLE_CARPET);
        willdrop.add(Material.PURPLE_WALL_BANNER);
        willdrop.add(Material.RAIL);
        willdrop.add(Material.REDSTONE_TORCH);
        willdrop.add(Material.REDSTONE_WALL_TORCH);
        willdrop.add(Material.RED_BANNER);
        willdrop.add(Material.RED_CARPET);
        willdrop.add(Material.RED_MUSHROOM);
        willdrop.add(Material.RED_TULIP);
        willdrop.add(Material.RED_WALL_BANNER);
        willdrop.add(Material.REPEATER);
        willdrop.add(Material.ROSE_BUSH);
        willdrop.add(Material.OAK_SIGN);
        willdrop.add(Material.DARK_OAK_SIGN);
        willdrop.add(Material.SPRUCE_SIGN);
        willdrop.add(Material.ACACIA_SIGN);
        willdrop.add(Material.JUNGLE_SIGN);
        willdrop.add(Material.BIRCH_SIGN);
        willdrop.add(Material.SPRUCE_BUTTON);
        willdrop.add(Material.SPRUCE_DOOR);
        willdrop.add(Material.SPRUCE_PRESSURE_PLATE);
        willdrop.add(Material.SPRUCE_SAPLING);
        willdrop.add(Material.SPRUCE_TRAPDOOR);
        willdrop.add(Material.STONE_BUTTON);
        willdrop.add(Material.STONE_PRESSURE_PLATE);
        willdrop.add(Material.SUGAR_CANE);
        willdrop.add(Material.SUNFLOWER);
        willdrop.add(Material.TALL_GRASS);
        willdrop.add(Material.TRIPWIRE_HOOK);
        willdrop.add(Material.VINE);
        willdrop.add(Material.OAK_WALL_SIGN);
        willdrop.add(Material.DARK_OAK_WALL_SIGN);
        willdrop.add(Material.SPRUCE_WALL_SIGN);
        willdrop.add(Material.ACACIA_WALL_SIGN);
        willdrop.add(Material.JUNGLE_WALL_SIGN);
        willdrop.add(Material.BIRCH_WALL_SIGN);
        willdrop.add(Material.WHEAT);
        willdrop.add(Material.WHITE_BANNER);
        willdrop.add(Material.WHITE_CARPET);
        willdrop.add(Material.WHITE_TULIP);
        willdrop.add(Material.WHITE_WALL_BANNER);
        willdrop.add(Material.YELLOW_BANNER);
        willdrop.add(Material.YELLOW_CARPET);
        willdrop.add(Material.YELLOW_WALL_BANNER);
        doors.add(Material.ACACIA_DOOR);
        doors.add(Material.BIRCH_DOOR);
        doors.add(Material.DARK_OAK_DOOR);
        doors.add(Material.IRON_DOOR);
        doors.add(Material.JUNGLE_DOOR);
        doors.add(Material.OAK_DOOR);
        doors.add(Material.SPRUCE_DOOR);
        plates.add(Material.ACACIA_PRESSURE_PLATE);
        plates.add(Material.BIRCH_PRESSURE_PLATE);
        plates.add(Material.DARK_OAK_PRESSURE_PLATE);
        plates.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        plates.add(Material.JUNGLE_PRESSURE_PLATE);
        plates.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        plates.add(Material.OAK_PRESSURE_PLATE);
        plates.add(Material.SPRUCE_PRESSURE_PLATE);
        plates.add(Material.STONE_PRESSURE_PLATE);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (!willdrop.contains(event.getBlock().getType())) {
            return;
        }
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled") || !plugin.getConfig().getBoolean("track_creative_place.attached_block")) {
            return;
        }
        Block block = event.getBlock();
        if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(block.getWorld().getName())) {
            return;
        }
        if (doors.contains(block.getType()) && plates.contains(event.getChangedType())) {
            return;
        }
        if (willdrop.contains(block.getType())) {
            String gmiwc = block.getWorld().getName() + "," + block.getChunk().getX() + "," + block.getChunk().getZ();
            if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
                return;
            }
            // check if the block was placed in creative
            if (plugin.getCreativeBlocks().get(gmiwc).contains(event.getBlock().getLocation().toString())) {
                event.setCancelled(true);
            }
        }
    }
}
