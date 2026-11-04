/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.entity.Player;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesBypass {

    public static boolean canBypassContainer(Player p, String action, GameModeInventories plugin) {
        String bypass = "container-" + action;
        return p.hasPermission("gamemodeinventories.bypass." + bypass) || canBypass(p, bypass, plugin);
    }

    public static boolean canBypass(Player p, String bypass, GameModeInventories plugin) {
        if (p.hasPermission("gamemodeinventories.bypass") || p.isOp()) {
            return plugin.getConfig().getBoolean("bypass." + bypass);
        } else {
            return false;
        }
    }
}
