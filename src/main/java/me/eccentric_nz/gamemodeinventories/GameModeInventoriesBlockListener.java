/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
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
        event.getEntity().getNearbyEntities(0.5, 0.5, 0.5).forEach((e) -> {
            if (e instanceof FallingBlock) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            Material mat = event.getItem().getType();
            Player p = event.getPlayer();
            if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                if (!plugin.getConfig().getBoolean("track_creative_place.enabled") || !plugin.getConfig().getBoolean("track_creative_place.no_seeds_from_pumpkin")) {
                    return;
                }
                if (mat.equals(Material.SHEARS)) {
                    Block pumpkin = event.getClickedBlock();
                    if (pumpkin == null) {
                        return;
                    }
                    if (!pumpkin.getType().equals(Material.PUMPKIN)) {
                        return;
                    }
                    if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(pumpkin.getWorld().getName())) {
                        return;
                    }
                    String gmiwc = pumpkin.getWorld().getName() + "," + pumpkin.getChunk().getX() + "," + pumpkin.getChunk().getZ();
                    if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
                        return;
                    }
                    if (plugin.getCreativeBlocks().get(gmiwc).contains(pumpkin.getLocation().toString())) {
                        event.setCancelled(true);
                    }
                }
            }
            if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                return;
            }
            if (plugin.getConfig().getBoolean("no_golem_spawn")) {
                Block pumpkin = event.getClickedBlock();
                if (pumpkin != null && pumpkin.getType().equals(Material.PUMPKIN)) {
                    // check blocks around pumpkin
                    if (mat.equals(Material.SHEARS) && GameModeInventoriesConstructedMob.checkBlocks(Material.PUMPKIN, pumpkin)) {
                        event.setCancelled(true);
                    }
                }
            }
            if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
                if (mat.equals(Material.ARMOR_STAND)) {
                    Block b = event.getClickedBlock();
                    if (b != null) {
                        if (plugin.getConfig().getStringList("track_creative_place.worlds").contains(b.getWorld().getName())) {
                            Location l = b.getLocation();
                            if (l != null) {
                                String gmip = l.getBlockX() + "," + l.getBlockZ();
                                plugin.getPoints().add(gmip);
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                    if (plugin.getPoints().contains(gmip)) {
                                        plugin.getPoints().remove(gmip);
                                    }
                                }, 600L);
                            }
                        }
                    }
                }
            }
            if (!plugin.getConfig().getBoolean("creative_blacklist")) {
                return;
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
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
    public void onEntityExplode(EntityExplodeEvent event) {
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(event.getLocation().getWorld().getName())) {
                return;
            }
            for (Block b : event.blockList()) {
                if (plugin.getNoTrackList().contains(b.getType())) {
                    continue;
                }
                String gmiwc = b.getWorld().getName() + "," + b.getChunk().getX() + "," + b.getChunk().getZ();
                if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
                    continue;
                }
                if (plugin.getCreativeBlocks().get(gmiwc).contains(b.getLocation().toString())) {
                    event.setYield(0);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
