/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesTrackBlackListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesTrackBlackListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        Player p = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType().equals(Material.AIR)) {
            return;
        }
        if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(block.getWorld().getName())) {
            return;
        }
        if (plugin.getNoTrackList().contains(block.getType())) {
            return;
        }
        String gmiwc = block.getWorld().getName() + "," + block.getChunk().getX() + "," + block.getChunk().getZ();
        if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
            return;
        }
        if (plugin.getCreativeBlocks().get(gmiwc).contains(block.getLocation().toString())) {
            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                plugin.getBlock().removeBlock(gmiwc, block.getLocation().toString());
            } else {
                String message;
                if (plugin.getConfig().getBoolean("track_creative_place.break_no_drop")) {
                    // remove the location from the creative blocks list because we're removing the block!
                    plugin.getBlock().removeBlock(gmiwc, block.getLocation().toString());
                    if (plugin.getBlockLogger().isLogging()) {
                        Location loc = block.getLocation();
                        String pname = p.getName();
                        switch (plugin.getBlockLogger().getWhichLogger()) {
                            case CORE_PROTECT: // log the block removal
                                Material type = block.getType();
                                BlockData data = block.getBlockData();
                                plugin.getBlockLogger().getCoreProtectAPI().logRemoval(pname, loc, type, data);
                                break;
                            case LOG_BLOCK:
                                plugin.getBlockLogger().getLogBlockConsumer().queueBlockBreak(pname, block.getState());
                                break;
                            case PRISM:
                                if (plugin.getBlockLogger().getPrism() != null) {
                                    GameModeInventoriesPrismHandler.log(loc, block, pname);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    block.setType(Material.AIR);
                    block.getDrops().clear();
                    message = plugin.getM().getMessage().get("NO_CREATIVE_DROPS");
                } else {
                    event.setCancelled(true);
                    message = plugin.getM().getMessage().get("NO_CREATIVE_BREAK");
                }
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + message);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (!p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        Material mat = event.getBlock().getType();
        if (plugin.getConfig().getBoolean("creative_blacklist") && plugin.getBlackList().contains(mat) && !GameModeInventoriesBypass.canBypass(p, "blacklist", plugin)) {
            event.setCancelled(true);
            if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                p.sendMessage(plugin.MY_PLUGIN_NAME + String.format(plugin.getM().getMessage().get("NO_CREATIVE_PLACE"), mat.toString()));
            }
            return;
        }
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            Block block = event.getBlock();
            if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(block.getWorld().getName())) {
                return;
            }
            if (plugin.getNoTrackList().contains(mat)) {
                return;
            }
            String gmiwc = block.getWorld().getName() + "," + block.getChunk().getX() + "," + block.getChunk().getZ();
            if (!plugin.getCreativeBlocks().containsKey(gmiwc) || !plugin.getCreativeBlocks().get(gmiwc).contains(block.getLocation().toString())) {
                plugin.getBlock().addBlock(gmiwc, block.getLocation().toString());
            }
        }
    }
}
