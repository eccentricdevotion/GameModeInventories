/*
 *  Copyright 2015 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.database;

import me.eccentric_nz.gamemodeinventories.GMIDebug;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

import java.sql.*;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesBlocksConverter {

    private final GameModeInventories plugin;

    public GameModeInventoriesBlocksConverter(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public static void processUpdateCounts(int[] updateCounts) {
        for (int i = 0; i < updateCounts.length; i++) {
            if (updateCounts[i] >= 0) {
                // Successfully executed; the number represents number of affected rows
            } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                // Successfully executed; number of affected rows not available
            } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                // Failed to execute
            }
        }
    }

    public void convertBlocksTable() {
        try {
            try (
                    Connection connection = plugin.getDatabaseConnection();
                    PreparedStatement statement = connection.prepareStatement("SELECT id, location FROM " + plugin.getPrefix() + "blocks");
                    ResultSet rs = statement.executeQuery();
            ) {
                if (rs.isBeforeFirst()) {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE " + plugin.getPrefix() + "blocks SET worldchunk = ? WHERE id = ?");) {
                        connection.setAutoCommit(false);
                        long count = 0;
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
                            count++;
                            if (count == 1000) {
                                // Execute the batch
                                int[] updateCounts = ps.executeBatch();
                                // All statements were successfully executed.
                                // updateCounts contains one element for each batched statement.
                                // updateCounts[i] contains the number of rows affected by that statement.
                                processUpdateCounts(updateCounts);
                                // Since there were no errors, commit
                                connection.commit();
                                count = 0;
                            }
                        }
                    }
                }
            } catch (BatchUpdateException ex) {
                // Not all of the statements were successfully executed
                int[] updateCounts = ex.getUpdateCounts();
                // Some databases will continue to execute after one fails.
                // If so, updateCounts.length will equal the number of batched statements.
                // If not, updateCounts.length will equal the number of successfully executed statements
                processUpdateCounts(updateCounts);
            }
        } catch (SQLException ex) {
            plugin.debug("Blocks updater error: " + ex.getMessage(), GMIDebug.ERROR);
        }
    }
}
