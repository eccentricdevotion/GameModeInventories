/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesWorldListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesWorldListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
//        if (plugin.getConfig().getBoolean("creative_world.switch_to")) {
//            Player p = event.getPlayer();
//            String uuid = p.getUniqueId().toString();
//            // player changed worlds, record last location
//            // check if the player has a record for this world
//            try (
//                    Connection connection = GameModeInventoriesConnectionPool.dbc();
//                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM worlds WHERE uuid = ? AND world = ?");
//            ) {
//                statement.setString(1, uuid);
//                statement.setString(2, p.getWorld().getName());
//                try (ResultSet rs = statement.executeQuery();) {
//                    if (rs.next()) {
//                        World w = plugin.getServer().getWorld(rs.getString("world"));
//                        if (w != null) {
//                            double x = rs.getDouble("x");
//                            double y = rs.getDouble("y");
//                            double z = rs.getDouble("z");
//                            float yaw = rs.getFloat("yaw");
//                            float pitch = rs.getFloat("pitch");
//                            Location loc = new Location(w, x, y, z, yaw, pitch);
//                            p.teleport(loc);
//                        }
//                    }
//                }
//            } catch (SQLException e) {
//                plugin.debug("Could not get creative world location, " + e);
//            }
//        }
        if (!plugin.getConfig().getBoolean("survival_on_world_change")) {
            return;
        }
        Player p = event.getPlayer();
        if (GameModeInventoriesBypass.canBypass(p, "survival", plugin)) {
            return;
        }
        World from = event.getFrom();
        World to = p.getWorld();
        if (from != to) {
            p.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (plugin.getConfig().getBoolean("creative_world.switch_to")) {
            // remember last location in from world
            Location from = event.getFrom();
            Location to = event.getTo();
            if (!from.getWorld().equals(to.getWorld())) {
                String uuid = event.getPlayer().getUniqueId().toString();
                // player changed worlds, record last location
                try (Connection connection = GameModeInventoriesConnectionPool.dbc();
                     // check if the player has a record for this world
                     PreparedStatement statement = connection.prepareStatement("SELECT world FROM " + plugin.getPrefix() + "worlds WHERE uuid = ? AND world = ?");) {
                    statement.setString(1, uuid);
                    statement.setString(2, from.getWorld().getName());
                    try (ResultSet rs = statement.executeQuery();) {
                        if (rs.isBeforeFirst()) {
                            rs.next();
                            // update the record
                            try (PreparedStatement update = connection.prepareStatement("UPDATE " + plugin.getPrefix() + "worlds set x = ?, y = ?, z = ?, pitch = ?, yaw = ? WHERE uuid = ? AND world = ?");) {
                                update.setDouble(1, from.getX());
                                update.setDouble(2, from.getY());
                                update.setDouble(3, from.getZ());
                                update.setDouble(4, from.getPitch());
                                update.setDouble(5, from.getYaw());
                                update.setString(6, uuid);
                                update.setString(7, from.getWorld().getName());
                                update.executeUpdate();
                            }
                        } else {
                            // add a new record
                            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO " + plugin.getPrefix() + "worlds (uuid, world, x, y, z, pitch, yaw) VALUES (?, ?, ?, ?, ?, ?, ?)");) {
                                insert.setString(1, uuid);
                                insert.setString(2, from.getWorld().getName());
                                insert.setDouble(3, from.getX());
                                insert.setDouble(4, from.getY());
                                insert.setDouble(5, from.getZ());
                                insert.setDouble(6, from.getPitch());
                                insert.setDouble(7, from.getYaw());
                                insert.executeUpdate();
                            }
                        }
                    }
                } catch (SQLException e) {
                    GameModeInventories.plugin.debug("Could not save world on teleport, " + e);
                }
            }
        }
    }
}
