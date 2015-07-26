/*
 *  Copyright 2015 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.eccentric_nz.gamemodeinventories.GMIDebug;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesBlocksConverter {

    private final GameModeInventories plugin;

    public GameModeInventoriesBlocksConverter(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void convertBlocksTable() {
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = GameModeInventoriesConnectionPool.dbc();
            statement = connection.prepareStatement("SELECT id, location FROM blocks");
            rs = statement.executeQuery();
            if (rs.isBeforeFirst()) {
                ps = connection.prepareStatement("UPDATE blocks SET worldchunk = ? WHERE id = ?");
                connection.setAutoCommit(false);
                while (rs.next()) {
                    String l = rs.getString("location");
                    // Location{world=CraftWorld{name=world},x=-87.0,y=61.0,z=237.0,pitch=0.0,yaw=0.0}
                    String[] first = l.split(",");
                    String[] wStr = first[0].split("=");
                    String[] xStr = first[1].split("=");
                    String[] zStr = first[3].split("=");
                    String w = wStr[2].substring(0, (wStr[2].length() - 1));
                    int x = Integer.parseInt(xStr[1].substring(0, (xStr[1].length() - 2))) >> 4;
                    int z = Integer.parseInt(zStr[1].substring(0, (zStr[1].length() - 2))) >> 4;
                    ps.setString(1, w + "," + x + "," + z);
                    ps.setInt(2, rs.getInt("id"));
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            plugin.debug("Blocks updater error: " + ex.getMessage(), GMIDebug.ERROR);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                plugin.debug("Blocks closing error: " + ex.getMessage(), GMIDebug.ERROR);
            }
        }
    }
}
