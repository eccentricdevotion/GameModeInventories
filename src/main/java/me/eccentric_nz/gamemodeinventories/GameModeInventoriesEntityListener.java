/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesEntityListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesEntityListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStandPlace(CreatureSpawnEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (event.getEntityType().equals(EntityType.ARMOR_STAND)) {
            Location l = event.getLocation();
            if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(l.getWorld().getName())) {
                return;
            }
            List<String> locs = Arrays.asList(l.getBlockX() + "," + l.getBlockZ(), (l.getBlockX()) + "," + (l.getBlockZ() - 1), (l.getBlockX() - 1) + "," + (l.getBlockZ()), (l.getBlockX()) + "," + (l.getBlockZ() + 1), (l.getBlockX() + 1) + "," + (l.getBlockZ()));
            for (String p : locs) {
                if (plugin.getPoints().contains(p)) {
                    plugin.getStands().add(event.getEntity().getUniqueId());
                    plugin.getPoints().remove(p);
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStandBreakOrCreativePVP(EntityDamageByEntityEvent event) {
        if (plugin.getConfig().getBoolean("no_creative_pvp") && event.getEntityType().equals(EntityType.PLAYER)) {
            Entity attacker = event.getDamager();
            if (attacker instanceof Player player) {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (plugin.getConfig().getBoolean("track_creative_place.enabled") && event.getEntityType().equals(EntityType.ARMOR_STAND)) {
            if (event.getEntity().getLastDamageCause() != null && plugin.getStands().contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                String message = plugin.getM().getMessage().get("NO_CREATIVE_BREAK");
                if (plugin.getConfig().getBoolean("track_creative_place.break_no_drop")) {
                    event.getEntity().remove();
                    message = plugin.getM().getMessage().get("NO_CREATIVE_DROPS");
                }
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    Entity damager = event.getDamager();
                    if (damager instanceof Player player) {
                        player.sendMessage(plugin.MY_PLUGIN_NAME + message);
                    }
                }
            }
        }
    }
}
