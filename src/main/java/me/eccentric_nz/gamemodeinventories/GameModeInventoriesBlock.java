/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesQueueData;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesRecordingQueue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesBlock {

    private final GameModeInventories plugin;

    public GameModeInventoriesBlock(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void addBlock(String gmiwc, String l) {
        GameModeInventoriesQueueData data = new GameModeInventoriesQueueData(gmiwc, l);
        if (GameModeInventoriesConnectionPool.isIsMySQL()) {
            GameModeInventoriesRecordingQueue.addToQueue(data);
        } else {
            saveBlockNow(data);
        }
        if (plugin.getCreativeBlocks().containsKey(gmiwc)) {
            plugin.getCreativeBlocks().get(gmiwc).add(l);
        } else {
            plugin.getCreativeBlocks().put(gmiwc, new ArrayList<>(Arrays.asList(l)));
        }
    }

    public void removeBlock(String gmiwc, String l) {
        String deleteQuery = "DELETE FROM " + plugin.getPrefix() + "blocks WHERE worldchunk = ? AND location = ?";
        try (
                Connection connection = GameModeInventoriesConnectionPool.dbc();
                PreparedStatement ps = connection.prepareStatement(deleteQuery);
        ) {
            ps.setString(1, gmiwc);
            ps.setString(2, l);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not remove block, " + e);
        }
        if (plugin.getCreativeBlocks().containsKey(gmiwc)) {
            plugin.getCreativeBlocks().get(gmiwc).remove(l);
        }
    }

    private void saveBlockNow(GameModeInventoriesQueueData data) {
        String insertQuery = "INSERT INTO " + plugin.getPrefix() + "blocks (worldchunk, location) VALUES (?,?)";
        try (
                Connection connection = GameModeInventoriesConnectionPool.dbc();
                PreparedStatement ps = connection.prepareStatement(insertQuery);
        ) {
            ps.setString(1, data.getWorldChunk());
            ps.setString(2, data.getLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not save block, " + e);
        }
    }
}
