/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        if (plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            try {
                Connection connection = GameModeInventoriesConnectionPool.dbc();
                String worldsQuery = "SELECT DISTINCT worldchunk FROM blocks";
                String blocksQuery = "SELECT * FROM blocks WHERE worldchunk = ?";
                Statement statement = connection.createStatement();
                PreparedStatement psb = connection.prepareStatement(blocksQuery);
                ResultSet rw = statement.executeQuery(worldsQuery);
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
                rw.close();
                psb.close();
                statement.close();
                if (GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Could not load blocks, " + e);
            }
        }
    }

    public void addBlock(String gmiwc, String l) {
        GameModeInventoriesQueueData data = new GameModeInventoriesQueueData(gmiwc, l);
        GameModeInventoriesRecordingQueue.addToQueue(data);
        if (plugin.getCreativeBlocks().containsKey(gmiwc)) {
            plugin.getCreativeBlocks().get(gmiwc).add(l);
        } else {
            plugin.getCreativeBlocks().put(gmiwc, new ArrayList<String>(Arrays.asList(l)));
        }
    }

    public void removeBlock(String gmiwc, String l) {
        try {
            Connection connection = GameModeInventoriesConnectionPool.dbc();
            String deleteQuery = "DELETE FROM blocks WHERE worldchunk = ? AND location = ?";
            PreparedStatement ps = connection.prepareStatement(deleteQuery);
            ps.setString(1, gmiwc);
            ps.setString(2, l);
            ps.executeUpdate();
            ps.close();
            if (GameModeInventoriesConnectionPool.isIsMySQL()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Could not remove block, " + e);
        }
        if (plugin.getCreativeBlocks().containsKey(gmiwc)) {
            plugin.getCreativeBlocks().get(gmiwc).remove(l);
        }
    }
}
