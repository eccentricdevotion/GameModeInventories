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
    private final List<Material> willDrop = new ArrayList<>();
    private final List<Material> doors = new ArrayList<>();
    private final List<Material> plates = new ArrayList<>();

    public GameModeInventoriesPhysicsListener(GameModeInventories plugin) {
        this.plugin = plugin;
        willDrop.add(Material.ACACIA_BUTTON);
        willDrop.add(Material.ACACIA_DOOR);
        willDrop.add(Material.ACACIA_PRESSURE_PLATE);
        willDrop.add(Material.ACACIA_SAPLING);
        willDrop.add(Material.ACACIA_SIGN);
        willDrop.add(Material.ACACIA_TRAPDOOR);
        willDrop.add(Material.ACACIA_WALL_SIGN);
        willDrop.add(Material.ACTIVATOR_RAIL);
        willDrop.add(Material.ALLIUM);
        willDrop.add(Material.AZURE_BLUET);
        willDrop.add(Material.BIRCH_BUTTON);
        willDrop.add(Material.BIRCH_DOOR);
        willDrop.add(Material.BIRCH_PRESSURE_PLATE);
        willDrop.add(Material.BIRCH_SAPLING);
        willDrop.add(Material.BIRCH_SIGN);
        willDrop.add(Material.BIRCH_TRAPDOOR);
        willDrop.add(Material.BIRCH_WALL_SIGN);
        willDrop.add(Material.BLACK_BANNER);
        willDrop.add(Material.BLACK_CARPET);
        willDrop.add(Material.BLACK_WALL_BANNER);
        willDrop.add(Material.BLUE_BANNER);
        willDrop.add(Material.BLUE_CARPET);
        willDrop.add(Material.BLUE_ORCHID);
        willDrop.add(Material.BLUE_WALL_BANNER);
        willDrop.add(Material.BROWN_BANNER);
        willDrop.add(Material.BROWN_CARPET);
        willDrop.add(Material.BROWN_MUSHROOM);
        willDrop.add(Material.BROWN_WALL_BANNER);
        willDrop.add(Material.COMPARATOR);
        willDrop.add(Material.CYAN_BANNER);
        willDrop.add(Material.CYAN_CARPET);
        willDrop.add(Material.CYAN_WALL_BANNER);
        willDrop.add(Material.DANDELION);
        willDrop.add(Material.DARK_OAK_BUTTON);
        willDrop.add(Material.DARK_OAK_DOOR);
        willDrop.add(Material.DARK_OAK_PRESSURE_PLATE);
        willDrop.add(Material.DARK_OAK_SAPLING);
        willDrop.add(Material.DARK_OAK_SIGN);
        willDrop.add(Material.DARK_OAK_TRAPDOOR);
        willDrop.add(Material.DARK_OAK_WALL_SIGN);
        willDrop.add(Material.DEAD_BUSH);
        willDrop.add(Material.DETECTOR_RAIL);
        willDrop.add(Material.FERN);
        willDrop.add(Material.FLOWER_POT);
        willDrop.add(Material.SHORT_GRASS);
        willDrop.add(Material.GRAY_BANNER);
        willDrop.add(Material.GRAY_CARPET);
        willDrop.add(Material.GRAY_WALL_BANNER);
        willDrop.add(Material.GREEN_BANNER);
        willDrop.add(Material.GREEN_CARPET);
        willDrop.add(Material.GREEN_WALL_BANNER);
        willDrop.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        willDrop.add(Material.IRON_DOOR);
        willDrop.add(Material.IRON_TRAPDOOR);
        willDrop.add(Material.JUNGLE_BUTTON);
        willDrop.add(Material.JUNGLE_DOOR);
        willDrop.add(Material.JUNGLE_PRESSURE_PLATE);
        willDrop.add(Material.JUNGLE_SAPLING);
        willDrop.add(Material.JUNGLE_SIGN);
        willDrop.add(Material.JUNGLE_TRAPDOOR);
        willDrop.add(Material.JUNGLE_WALL_SIGN);
        willDrop.add(Material.LADDER);
        willDrop.add(Material.LARGE_FERN);
        willDrop.add(Material.LEVER);
        willDrop.add(Material.LIGHT_BLUE_BANNER);
        willDrop.add(Material.LIGHT_BLUE_CARPET);
        willDrop.add(Material.LIGHT_BLUE_WALL_BANNER);
        willDrop.add(Material.LIGHT_GRAY_BANNER);
        willDrop.add(Material.LIGHT_GRAY_CARPET);
        willDrop.add(Material.LIGHT_GRAY_WALL_BANNER);
        willDrop.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        willDrop.add(Material.LILAC);
        willDrop.add(Material.LIME_BANNER);
        willDrop.add(Material.LIME_CARPET);
        willDrop.add(Material.LIME_WALL_BANNER);
        willDrop.add(Material.MAGENTA_BANNER);
        willDrop.add(Material.MAGENTA_CARPET);
        willDrop.add(Material.MAGENTA_WALL_BANNER);
        willDrop.add(Material.NETHER_WART);
        willDrop.add(Material.OAK_BUTTON);
        willDrop.add(Material.OAK_DOOR);
        willDrop.add(Material.OAK_PRESSURE_PLATE);
        willDrop.add(Material.OAK_SAPLING);
        willDrop.add(Material.OAK_SIGN);
        willDrop.add(Material.OAK_TRAPDOOR);
        willDrop.add(Material.OAK_WALL_SIGN);
        willDrop.add(Material.ORANGE_BANNER);
        willDrop.add(Material.ORANGE_CARPET);
        willDrop.add(Material.ORANGE_TULIP);
        willDrop.add(Material.ORANGE_WALL_BANNER);
        willDrop.add(Material.OXEYE_DAISY);
        willDrop.add(Material.PAINTING);
        willDrop.add(Material.PEONY);
        willDrop.add(Material.PINK_BANNER);
        willDrop.add(Material.PINK_CARPET);
        willDrop.add(Material.PINK_TULIP);
        willDrop.add(Material.PINK_WALL_BANNER);
        willDrop.add(Material.POPPY);
        willDrop.add(Material.PURPLE_BANNER);
        willDrop.add(Material.PURPLE_CARPET);
        willDrop.add(Material.PURPLE_WALL_BANNER);
        willDrop.add(Material.RAIL);
        willDrop.add(Material.RED_BANNER);
        willDrop.add(Material.RED_CARPET);
        willDrop.add(Material.RED_MUSHROOM);
        willDrop.add(Material.RED_TULIP);
        willDrop.add(Material.RED_WALL_BANNER);
        willDrop.add(Material.REDSTONE_TORCH);
        willDrop.add(Material.REDSTONE_WALL_TORCH);
        willDrop.add(Material.REPEATER);
        willDrop.add(Material.ROSE_BUSH);
        willDrop.add(Material.SPRUCE_BUTTON);
        willDrop.add(Material.SPRUCE_DOOR);
        willDrop.add(Material.SPRUCE_PRESSURE_PLATE);
        willDrop.add(Material.SPRUCE_SAPLING);
        willDrop.add(Material.SPRUCE_SIGN);
        willDrop.add(Material.SPRUCE_TRAPDOOR);
        willDrop.add(Material.SPRUCE_WALL_SIGN);
        willDrop.add(Material.STONE_BUTTON);
        willDrop.add(Material.STONE_PRESSURE_PLATE);
        willDrop.add(Material.SUGAR_CANE);
        willDrop.add(Material.SUNFLOWER);
        willDrop.add(Material.TALL_GRASS);
        willDrop.add(Material.TRIPWIRE_HOOK);
        willDrop.add(Material.VINE);
        willDrop.add(Material.WHEAT);
        willDrop.add(Material.WHITE_BANNER);
        willDrop.add(Material.WHITE_CARPET);
        willDrop.add(Material.WHITE_TULIP);
        willDrop.add(Material.WHITE_WALL_BANNER);
        willDrop.add(Material.YELLOW_BANNER);
        willDrop.add(Material.YELLOW_CARPET);
        willDrop.add(Material.YELLOW_WALL_BANNER);
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
        Block block = event.getBlock();
        if (block == null) {
            return;
        }
        if (!willDrop.contains(block.getType())) {
            return;
        }
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled") || !plugin.getConfig().getBoolean("track_creative_place.attached_block")) {
            return;
        }
        if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(block.getWorld().getName())) {
            return;
        }
        if (doors.contains(block.getType()) && plates.contains(event.getChangedType())) {
            return;
        }
        if (willDrop.contains(block.getType())) {
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
