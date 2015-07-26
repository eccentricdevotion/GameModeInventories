/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesQueueData;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesRecordingQueue;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesBlock {

    private final GameModeInventories plugin;

    public GameModeInventoriesBlock(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void loadBlocks() {
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement psb = null;
        ResultSet rw = null;
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            try {
                connection = GameModeInventoriesConnectionPool.dbc();
                String worldsQuery = "SELECT DISTINCT worldchunk FROM blocks";
                String blocksQuery = "SELECT * FROM blocks WHERE worldchunk = ?";
                statement = connection.prepareStatement(worldsQuery);
                psb = connection.prepareStatement(blocksQuery);
                rw = statement.executeQuery();
                if (rw.isBeforeFirst()) {
                    while (rw.next()) {
                        String w = rw.getString("worldchunk");
                        psb.setString(1, w);
                        ResultSet rb = psb.executeQuery();
                        List<String> l = new ArrayList<String>();
                        if (rb.isBeforeFirst()) {
                            while (rb.next()) {
                                l.add(rb.getString("location"));
                            }
                        }
                        plugin.getCreativeBlocks().put(w, l);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Could not load blocks, " + e);
            } finally {
                try {
                    if (rw != null) {
                        rw.close();
                    }
                    if (psb != null) {
                        psb.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Could not load blocks, " + e);
                }
            }
        }
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
            plugin.getCreativeBlocks().put(gmiwc, new ArrayList<String>(Arrays.asList(l)));
        }
    }

    public void removeBlock(String gmiwc, String l) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = GameModeInventoriesConnectionPool.dbc();
            String deleteQuery = "DELETE FROM blocks WHERE worldchunk = ? AND location = ?";
            ps = connection.prepareStatement(deleteQuery);
            ps.setString(1, gmiwc);
            ps.setString(2, l);
            ps.executeUpdate();

            if (GameModeInventoriesConnectionPool.isIsMySQL()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Could not remove block, " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Could not remove block, " + e);
            }
        }
        if (plugin.getCreativeBlocks().containsKey(gmiwc)) {
            plugin.getCreativeBlocks().get(gmiwc).remove(l);
        }
    }

    private void saveBlockNow(GameModeInventoriesQueueData data) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = GameModeInventoriesConnectionPool.dbc();
            String insertQuery = "INSERT INTO blocks (worldchunk, location) VALUES (?,?)";
            ps = connection.prepareStatement(insertQuery);
            ps.setString(1, data.getWorldChunk());
            ps.setString(2, data.getLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not save block, " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Could not remove block, " + e);
            }
        }
    }
}
