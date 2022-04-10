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

    public GameModeInventoriesStand(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void loadStands() {
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            try (
                    Connection connection = GameModeInventoriesConnectionPool.dbc();
                    PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM " + plugin.getPrefix() + "stands");
                    ResultSet rs = statement.executeQuery();
            ) {
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        plugin.getStands().add(UUID.fromString(rs.getString("uuid")));
                    }
                }
                // clear stands
                try (PreparedStatement ps = connection.prepareStatement("DELETE FROM " + plugin.getPrefix() + "stands");) {
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                System.err.println("Could not load stands, " + e);
            }
        }
    }

    public void saveStands() {
        try (
                Connection connection = GameModeInventoriesConnectionPool.dbc();
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + plugin.getPrefix() + "stands (uuid) VALUES (?)");
        ) {
            for (UUID uuid : plugin.getStands()) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Could not save stands, " + e);
        }
    }
}
