/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.entity.Player;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesBypass {

    public static boolean canBypass(Player p, String bypass, GameModeInventories plugin) {
        if (p.hasPermission("gamemodeinventories.bypass") || p.isOp()) {
            return plugin.getConfig().getBoolean("bypass." + bypass);
        } else {
            return false;
        }
    }
}
