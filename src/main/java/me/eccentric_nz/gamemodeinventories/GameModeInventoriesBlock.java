/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesBlock {

    private final GameModeInventories plugin;
    GameModeInventoriesDBConnection service = GameModeInventoriesDBConnection.getInstance();

    public GameModeInventoriesBlock(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void loadBlocks() {
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            try {
                Connection connection = service.getConnection();
                service.testConnection(connection);
                String blocksQuery = "SELECT location FROM blocks";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(blocksQuery);
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        plugin.getCreativeBlocks().add(rs.getString("location"));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Could not save block, " + e);
            }
        }
    }

    public void addBlock(String l) {
        try {
            Connection connection = service.getConnection();
            service.testConnection(connection);
            String insertQuery = "INSERT INTO blocks (location) VALUES (?)";
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            ps.setString(1, l);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not save block, " + e);
        }
        plugin.getCreativeBlocks().add(l);
    }

    public void removeBlock(String l) {
        try {
            Connection connection = service.getConnection();
            service.testConnection(connection);
            String deleteQuery = "DELETE FROM blocks WHERE location = ?";
            PreparedStatement ps = connection.prepareStatement(deleteQuery);
            ps.setString(1, l);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not remove block, " + e);
        }
        plugin.getCreativeBlocks().remove(l);
    }
}
