/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesStand {

    private final GameModeInventories plugin;
    private Connection connection = null;

    public GameModeInventoriesStand(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void loadStands() {
        PreparedStatement statement = null;
        ResultSet rs = null;
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            try {
                connection = GameModeInventoriesConnectionPool.dbc();
                statement = connection.prepareStatement("SELECT uuid FROM stands");
                rs = statement.executeQuery();
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        plugin.getStands().add(UUID.fromString(rs.getString("uuid")));
                    }
                }
                // clear stands
                statement = connection.prepareStatement("DELETE FROM stands");
                statement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Could not load stands, " + e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    plugin.debug("Error closing stands statement or resultset: " + ex.getMessage());
                }
            }
        }
    }

    public void saveStands() {
        PreparedStatement ps = null;
        try {
            connection = GameModeInventoriesConnectionPool.dbc();
            ps = connection.prepareStatement("INSERT INTO stands (uuid) VALUES (?)");
            for (UUID uuid : plugin.getStands()) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Could not save stands, " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                plugin.debug("Error closing stands statement: " + ex.getMessage());
            }
        }
    }
}
