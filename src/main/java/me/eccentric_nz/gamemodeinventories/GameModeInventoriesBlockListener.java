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
import org.bukkit.entity.Player;
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
        Player p = event.getPlayer();
        if (!p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (event.hasItem()) {
            if (plugin.getConfig().getBoolean("track_creative_place.enabled") && event.getItem().getType().equals(Material.ARMOR_STAND)) {
                Block b = event.getClickedBlock();
                if (b != null) {
                    if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(b.getWorld().getName())) {
                        return;
                    }
                    Location l = b.getLocation();
                    if (l != null) {
                        final String gmip = l.getBlockX() + "," + l.getBlockZ();
                        plugin.getPoints().add(gmip);
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if (plugin.getPoints().contains(gmip)) {
                                    plugin.getPoints().remove(gmip);
                                }
                            }
                        }, 600L);
                    }
                }
            }
            if (!plugin.getConfig().getBoolean("creative_blacklist")) {
                return;
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                Material mat = event.getItem().getType();
                if (plugin.getBlackList().contains(mat) && !GameModeInventoriesBypass.canBypass(p, "blacklist", plugin)) {
                    event.setCancelled(true);
                    event.setUseItemInHand(Result.DENY);
                    if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                        p.sendMessage(plugin.MY_PLUGIN_NAME + String.format(plugin.getM().getMessage().get("NO_CREATIVE_PLACE"), mat.toString()));
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
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
        if (plugin.getCreativeBlocks().contains(block.getLocation().toString())) {
            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                plugin.getBlock().removeBlock(block.getLocation().toString());
            } else {
                String message;
                if (plugin.getConfig().getBoolean("track_creative_place.break_no_drop")) {
                    // remove the location from the creative blocks list because we're removing the block!
                    plugin.getBlock().removeBlock(block.getLocation().toString());
                    if (plugin.getBlockLogger().isLogging()) {
                        Location loc = block.getLocation();
                        String pname = p.getName();
                        switch (plugin.getBlockLogger().getWhichLogger()) {
                            case CORE_PROTECT: // log the block removal
                                int type = block.getTypeId();
                                byte data = block.getData();
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
            if (!plugin.getCreativeBlocks().contains(block.getLocation().toString())) {
                plugin.getBlock().addBlock(block.getLocation().toString());
            }
        }
    }
}
