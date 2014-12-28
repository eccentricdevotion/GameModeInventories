/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesStand {

    private final GameModeInventories plugin;
    GameModeInventoriesDBConnection service = GameModeInventoriesDBConnection.getInstance();
    Connection connection = service.getConnection();
    private Statement statement = null;
    private ResultSet rs = null;
    private PreparedStatement ps = null;

    public GameModeInventoriesStand(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void loadStands() {
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            try {
                service.testConnection(connection);
                String standsQuery = "SELECT uuid FROM stands";
                statement = connection.createStatement();
                rs = statement.executeQuery(standsQuery);
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        plugin.getStands().add(UUID.fromString(rs.getString("uuid")));
                    }
                }
                // clear stands
                statement.executeUpdate("DELETE FROM stands");
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
                } catch (SQLException ex) {
                    plugin.debug("Error closing stands statement or resultset: " + ex.getMessage());
                }
            }
        }
    }

    public void saveStands() {
        try {
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
            } catch (SQLException ex) {
                plugin.debug("Error closing stands statement: " + ex.getMessage());
            }
        }
    }
}
