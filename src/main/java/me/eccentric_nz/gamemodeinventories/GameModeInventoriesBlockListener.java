/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesBlockListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesBlockListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (!plugin.getConfig().getBoolean("no_falling_drops")) {
            return;
        }
        for (Entity e : event.getEntity().getNearbyEntities(0.5, 0.5, 0.5)) {
            if (e instanceof FallingBlock) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (event.hasItem() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material mat = event.getItem().getType();
            if (plugin.getBlackList().contains(mat)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    event.getPlayer().sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + mat.toString() + " placement is disabled in creative gamemode!");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                plugin.getBlock().removeBlock(event.getBlock().getLocation().toString());
            } else {
                event.setCancelled(true);
                String message = "You cannot break blocks that were placed in creative gamemode!";
                if (plugin.getConfig().getBoolean("track_creative_place.break_no_drop")) {
                    // remove the location from the creative blocks list because we're removing the block!
                    plugin.getBlock().removeBlock(event.getBlock().getLocation().toString());
                    event.getBlock().setType(Material.AIR);
                    message = "Blocks that were placed in creative gamemode, do not give drops!";
                }
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    event.getPlayer().sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + message);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockplace(BlockPlaceEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        Material mat = event.getBlock().getType();
        if (plugin.getConfig().getBoolean("creative_blacklist") && plugin.getBlackList().contains(mat)) {
            event.setCancelled(true);
            if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                event.getPlayer().sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + mat.toString() + " placement is disabled in creative gamemode!");
            }
            return;
        }
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            if (!plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
                plugin.getBlock().addBlock(event.getBlock().getLocation().toString());
            }
        }
    }
}
