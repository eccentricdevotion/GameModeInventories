/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(ignoreCancelled = true)
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
        if (!plugin.getConfig().getBoolean("creative_blacklist")) {
            return;
        }
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (event.hasItem() && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            Material mat = event.getItem().getType();
            if (plugin.getBlackList().contains(mat)) {
                event.setCancelled(true);
                event.setUseItemInHand(Result.DENY);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    event.getPlayer().sendMessage(plugin.MY_PLUGIN_NAME + String.format(plugin.getM().getMessage().get("NO_CREATIVE_PLACE"), mat.toString()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (plugin.getCreativeBlocks().contains(event.getBlock().getLocation().toString())) {
            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                plugin.getBlock().removeBlock(event.getBlock().getLocation().toString());
            } else {
                String message;
                if (plugin.getConfig().getBoolean("track_creative_place.break_no_drop")) {
                    // remove the location from the creative blocks list because we're removing the block!
                    plugin.getBlock().removeBlock(event.getBlock().getLocation().toString());
                    event.getBlock().setType(Material.AIR);
                    event.getBlock().getDrops().clear();
                    message = plugin.getM().getMessage().get("NO_CREATIVE_DROPS");
                    if (plugin.getCPAPI() != null) {
                        // log the block removal
                        Location loc = event.getBlock().getLocation();
                        int type = event.getBlock().getTypeId();
                        byte data = event.getBlock().getData();
                        plugin.getCPAPI().logRemoval(event.getPlayer().getName(), loc, type, data);
                    }
                } else {
                    event.setCancelled(true);
                    message = plugin.getM().getMessage().get("NO_CREATIVE_BREAK");
                }
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    event.getPlayer().sendMessage(plugin.MY_PLUGIN_NAME + message);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBedrockBreak(BlockBreakEvent event) {
        if (plugin.getConfig().getBoolean("break_bedrock")) {
            return;
        }
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        Block br = event.getBlock();
        if (!br.getType().equals(Material.BEDROCK)) {
            return;
        }
        if (br.getLocation().getY() < 5) {
            event.setCancelled(true);
        } else if (br.getWorld().getEnvironment().equals(Environment.NETHER) && br.getLocation().getY() > 122) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        Material mat = event.getBlock().getType();
        if (plugin.getConfig().getBoolean("creative_blacklist") && plugin.getBlackList().contains(mat)) {
            event.setCancelled(true);
            if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                event.getPlayer().sendMessage(plugin.MY_PLUGIN_NAME + String.format(plugin.getM().getMessage().get("NO_CREATIVE_PLACE"), mat.toString()));
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
