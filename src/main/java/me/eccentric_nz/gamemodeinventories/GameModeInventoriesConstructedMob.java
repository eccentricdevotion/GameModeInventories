package me.eccentric_nz.gamemodeinventories;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.List;

public class GameModeInventoriesConstructedMob {

    public static boolean checkBlocks(Material trigger, Block block) {
        switch (trigger) {
            case PUMPKIN:
            case CARVED_PUMPKIN:
            case JACK_O_LANTERN:
                return isGolem(block);
            case WITHER_SKELETON_SKULL:
            case WITHER_SKELETON_WALL_SKULL:
                return isWither(block, trigger);
            default:
                return false;
        }
    }

    private static final List<BlockFace> faces = Arrays.asList(BlockFace.DOWN, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP);

    private static boolean isGolem(Block block) {
        BlockFace direction = BlockFace.DOWN;
        for (BlockFace face : faces) {
            if (block.getRelative(face).getType().equals(Material.IRON_BLOCK) && block.getRelative(face, 2).equals(Material.IRON_BLOCK)) {
                direction = face;
                break;
            }
        }
        switch (direction) {
            case DOWN:
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                } else if (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                }
            case EAST:
                if (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                }
            case NORTH:
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                }
            case WEST:
                if (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                }
            case SOUTH:
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                }
            case UP:
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                } else if (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getType().equals(Material.IRON_BLOCK) && block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getType().equals(Material.IRON_BLOCK)) {
                    return true;
                }
            default:
                return false;
        }
    }

    private static boolean isWither(Block block, Material trigger) {
        Block skull = block;
        // get middle skull
        for (BlockFace face : faces) {
            if (block.getRelative(face).getType().equals(trigger) && block.getRelative(face, 2).getType().equals(trigger)) {
                skull = block.getRelative(face);
                break;
            }
        }
        BlockFace direction = BlockFace.DOWN;
        for (BlockFace face : faces) {
            if (skull.getRelative(face).getType().equals(Material.SOUL_SAND) && skull.getRelative(face, 2).getType().equals(Material.SOUL_SAND)) {
                direction = face;
                break;
            }
        }
        switch (direction) {
            case DOWN:
                if (skull.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getType().equals(Material.SOUL_SAND)) {
                    return true;
                } else if (skull.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getType().equals(Material.SOUL_SAND)) {
                    return true;
                }
            case EAST:
                if (skull.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getType().equals(Material.SOUL_SAND)) {
                    return true;
                }
            case NORTH:
                if (skull.getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH).getType().equals(Material.SOUL_SAND)) {
                    return true;
                }
            case WEST:
                if (skull.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getType().equals(Material.SOUL_SAND)) {
                    return true;
                }
            case SOUTH:
                if (skull.getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH).getType().equals(Material.SOUL_SAND)) {
                    return true;
                }
            case UP:
                if (skull.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getType().equals(Material.SOUL_SAND)) {
                    return true;
                } else if (skull.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getType().equals(Material.SOUL_SAND) && skull.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getType().equals(Material.SOUL_SAND)) {
                    return true;
                }
            default:
                return false;
        }
    }
}
