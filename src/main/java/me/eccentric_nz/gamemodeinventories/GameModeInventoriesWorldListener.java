/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesWorldListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesWorldListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!plugin.getConfig().getBoolean("survival_on_world_change")) {
            return;
        }
        World from = event.getFrom();
        World to = event.getPlayer().getWorld();
        if (from != to) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }
}
