/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesPhysicsListener implements Listener {

    private final GameModeInventories plugin;
    private final List<Material> willdrop = new ArrayList<>();
    private final List<Material> doors = new ArrayList<>();
    private final List<Material> plates = new ArrayList<>();

    public GameModeInventoriesPhysicsListener(GameModeInventories plugin) {
        this.plugin = plugin;
        this.willdrop.add(Material.ACACIA_BUTTON);
        this.willdrop.add(Material.ACACIA_DOOR);
        this.willdrop.add(Material.ACACIA_PRESSURE_PLATE);
        this.willdrop.add(Material.ACACIA_SAPLING);
        this.willdrop.add(Material.ACACIA_TRAPDOOR);
        this.willdrop.add(Material.ACTIVATOR_RAIL);
        this.willdrop.add(Material.ALLIUM);
        this.willdrop.add(Material.AZURE_BLUET);
        this.willdrop.add(Material.BIRCH_BUTTON);
        this.willdrop.add(Material.BIRCH_DOOR);
        this.willdrop.add(Material.BIRCH_PRESSURE_PLATE);
        this.willdrop.add(Material.BIRCH_SAPLING);
        this.willdrop.add(Material.BIRCH_TRAPDOOR);
        this.willdrop.add(Material.BLACK_BANNER);
        this.willdrop.add(Material.BLACK_CARPET);
        this.willdrop.add(Material.BLACK_WALL_BANNER);
        this.willdrop.add(Material.BLUE_BANNER);
        this.willdrop.add(Material.BLUE_CARPET);
        this.willdrop.add(Material.BLUE_ORCHID);
        this.willdrop.add(Material.BLUE_WALL_BANNER);
        this.willdrop.add(Material.BROWN_BANNER);
        this.willdrop.add(Material.BROWN_CARPET);
        this.willdrop.add(Material.BROWN_MUSHROOM);
        this.willdrop.add(Material.BROWN_WALL_BANNER);
        this.willdrop.add(Material.COMPARATOR);
        this.willdrop.add(Material.CYAN_BANNER);
        this.willdrop.add(Material.CYAN_CARPET);
        this.willdrop.add(Material.CYAN_WALL_BANNER);
        this.willdrop.add(Material.DANDELION);
        this.willdrop.add(Material.DARK_OAK_BUTTON);
        this.willdrop.add(Material.DARK_OAK_DOOR);
        this.willdrop.add(Material.DARK_OAK_PRESSURE_PLATE);
        this.willdrop.add(Material.DARK_OAK_SAPLING);
        this.willdrop.add(Material.DARK_OAK_TRAPDOOR);
        this.willdrop.add(Material.DEAD_BUSH);
        this.willdrop.add(Material.DETECTOR_RAIL);
        this.willdrop.add(Material.FERN);
        this.willdrop.add(Material.FLOWER_POT);
        this.willdrop.add(Material.GRASS);
        this.willdrop.add(Material.GRAY_BANNER);
        this.willdrop.add(Material.GRAY_CARPET);
        this.willdrop.add(Material.GRAY_WALL_BANNER);
        this.willdrop.add(Material.GREEN_BANNER);
        this.willdrop.add(Material.GREEN_CARPET);
        this.willdrop.add(Material.GREEN_WALL_BANNER);
        this.willdrop.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        this.willdrop.add(Material.IRON_DOOR);
        this.willdrop.add(Material.IRON_TRAPDOOR);
        this.willdrop.add(Material.JUNGLE_BUTTON);
        this.willdrop.add(Material.JUNGLE_DOOR);
        this.willdrop.add(Material.JUNGLE_PRESSURE_PLATE);
        this.willdrop.add(Material.JUNGLE_SAPLING);
        this.willdrop.add(Material.JUNGLE_TRAPDOOR);
        this.willdrop.add(Material.LADDER);
        this.willdrop.add(Material.LARGE_FERN);
        this.willdrop.add(Material.LEVER);
        this.willdrop.add(Material.LIGHT_BLUE_BANNER);
        this.willdrop.add(Material.LIGHT_BLUE_CARPET);
        this.willdrop.add(Material.LIGHT_BLUE_WALL_BANNER);
        this.willdrop.add(Material.LIGHT_GRAY_BANNER);
        this.willdrop.add(Material.LIGHT_GRAY_CARPET);
        this.willdrop.add(Material.LIGHT_GRAY_WALL_BANNER);
        this.willdrop.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.willdrop.add(Material.LILAC);
        this.willdrop.add(Material.LIME_BANNER);
        this.willdrop.add(Material.LIME_CARPET);
        this.willdrop.add(Material.LIME_WALL_BANNER);
        this.willdrop.add(Material.MAGENTA_BANNER);
        this.willdrop.add(Material.MAGENTA_CARPET);
        this.willdrop.add(Material.MAGENTA_WALL_BANNER);
        this.willdrop.add(Material.NETHER_WART);
        this.willdrop.add(Material.OAK_BUTTON);
        this.willdrop.add(Material.OAK_PRESSURE_PLATE);
        this.willdrop.add(Material.OAK_SAPLING);
        this.willdrop.add(Material.OAK_TRAPDOOR);
        this.willdrop.add(Material.ORANGE_BANNER);
        this.willdrop.add(Material.ORANGE_CARPET);
        this.willdrop.add(Material.ORANGE_TULIP);
        this.willdrop.add(Material.ORANGE_WALL_BANNER);
        this.willdrop.add(Material.OXEYE_DAISY);
        this.willdrop.add(Material.PAINTING);
        this.willdrop.add(Material.PEONY);
        this.willdrop.add(Material.PINK_BANNER);
        this.willdrop.add(Material.PINK_CARPET);
        this.willdrop.add(Material.PINK_TULIP);
        this.willdrop.add(Material.PINK_WALL_BANNER);
        this.willdrop.add(Material.POPPY);
        this.willdrop.add(Material.PURPLE_BANNER);
        this.willdrop.add(Material.PURPLE_CARPET);
        this.willdrop.add(Material.PURPLE_WALL_BANNER);
        this.willdrop.add(Material.RAIL);
        this.willdrop.add(Material.REDSTONE_TORCH);
        this.willdrop.add(Material.REDSTONE_WALL_TORCH);
        this.willdrop.add(Material.RED_BANNER);
        this.willdrop.add(Material.RED_CARPET);
        this.willdrop.add(Material.RED_MUSHROOM);
        this.willdrop.add(Material.RED_TULIP);
        this.willdrop.add(Material.RED_WALL_BANNER);
        this.willdrop.add(Material.REPEATER);
        this.willdrop.add(Material.ROSE_BUSH);
        this.willdrop.add(Material.SIGN);
        this.willdrop.add(Material.SPRUCE_BUTTON);
        this.willdrop.add(Material.SPRUCE_DOOR);
        this.willdrop.add(Material.SPRUCE_PRESSURE_PLATE);
        this.willdrop.add(Material.SPRUCE_SAPLING);
        this.willdrop.add(Material.SPRUCE_TRAPDOOR);
        this.willdrop.add(Material.STONE_BUTTON);
        this.willdrop.add(Material.STONE_PRESSURE_PLATE);
        this.willdrop.add(Material.SUGAR_CANE);
        this.willdrop.add(Material.SUNFLOWER);
        this.willdrop.add(Material.TALL_GRASS);
        this.willdrop.add(Material.TRIPWIRE_HOOK);
        this.willdrop.add(Material.VINE);
        this.willdrop.add(Material.WALL_SIGN);
        this.willdrop.add(Material.WHEAT);
        this.willdrop.add(Material.WHITE_BANNER);
        this.willdrop.add(Material.WHITE_CARPET);
        this.willdrop.add(Material.WHITE_TULIP);
        this.willdrop.add(Material.WHITE_WALL_BANNER);
        this.willdrop.add(Material.YELLOW_BANNER);
        this.willdrop.add(Material.YELLOW_CARPET);
        this.willdrop.add(Material.YELLOW_WALL_BANNER);
        this.doors.add(Material.ACACIA_DOOR);
        this.doors.add(Material.BIRCH_DOOR);
        this.doors.add(Material.DARK_OAK_DOOR);
        this.doors.add(Material.IRON_DOOR);
        this.doors.add(Material.JUNGLE_DOOR);
        this.doors.add(Material.OAK_DOOR);
        this.doors.add(Material.SPRUCE_DOOR);
        this.plates.add(Material.ACACIA_PRESSURE_PLATE);
        this.plates.add(Material.BIRCH_PRESSURE_PLATE);
        this.plates.add(Material.DARK_OAK_PRESSURE_PLATE);
        this.plates.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        this.plates.add(Material.JUNGLE_PRESSURE_PLATE);
        this.plates.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.plates.add(Material.OAK_PRESSURE_PLATE);
        this.plates.add(Material.SPRUCE_PRESSURE_PLATE);
        this.plates.add(Material.STONE_PRESSURE_PLATE);
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
