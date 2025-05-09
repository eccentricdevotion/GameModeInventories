package me.eccentric_nz.gamemodeinventories.database;

import me.eccentric_nz.gamemodeinventories.GameModeInventories;
import me.eccentric_nz.gamemodeinventories.GameModeInventoriesXPCalculator;
import org.bukkit.util.FileUtil;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class GameModeInventoriesXPConverter {
    private final GameModeInventories plugin;
    private static final int BATCH_SIZE = 1000;

    public GameModeInventoriesXPConverter(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    // Old methods optimized a bit
    private static int oldGetLevelForExp(double oldExp) {
        return (oldExp < 255)
            ? (int)(oldExp / 17)
            : (oldExp < 887)
                ? (int)((29.5 + Math.sqrt(29.5 * 29.5 - 4 * 1.5 * (360 - oldExp))) / (2 * 1.5))
                : (int)((151.5 + Math.sqrt(151.5 * 151.5 - 4 * 3.5 * (2220 - oldExp))) / (2 * 3.5));
    }

    private static double oldGetXpNeededToLevelUp(int oldLevel) {
        return oldLevel > 30
            ? 62.0 + (oldLevel - 30.0) * 7.0
            : oldLevel >= 16
                ? 17.0 + (oldLevel - 15.0) * 3.0
                : 17.0;
    }

    private static double oldGetXpForLevel(int oldLevel) {
        return oldLevel >= 30
            ? 3.5 * oldLevel * oldLevel - 151.5 * oldLevel + 2220
            : oldLevel >= 16
                ? 1.5 * oldLevel * oldLevel - 29.5 * oldLevel + 360
                : 17.0 * oldLevel;
    }

    // Conversion
    private double convertXp(double oldXp) {
        if(oldXp < 0) return 0;
        int oldLevel = oldGetLevelForExp(oldXp);
        double oldPct = (oldXp - oldGetXpForLevel(oldLevel)) / oldGetXpNeededToLevelUp(oldLevel);
        return GameModeInventoriesXPCalculator.getXpForLevel(oldLevel) + Math.round(GameModeInventoriesXPCalculator.getXpNeededToLevelUp(oldLevel) * oldPct);
    }

    private boolean backupDBSqlite() {
        plugin.getLogger().info("[GameModeInventories] Backing up GMI database...");
        File oldFile = new File(plugin.getDataFolder(), "GMI.db");

        if (!oldFile.exists()) {
            return false;
        }

        File newFile = new File(plugin.getDataFolder(), "GMI_" + System.currentTimeMillis() + ".db");

        FileUtil.copy(oldFile, newFile);
        return true;
    }

    public boolean convertXPColumn() {
        if(plugin.getConfig().getString("storage.database").equalsIgnoreCase("sqlite")) {
            if(backupDBSqlite()) {
                plugin.getLogger().info("[GameModeInventories] SQLite backup completed successfully.");
            }else{
                plugin.getLogger().severe("[GameModeInventories] SQLite backup failed: GMI.db not found or could not be accessed.");
                return false;
            }
        }
        Connection connection = plugin.getDatabaseConnection();
        String tableName = plugin.getPrefix() + "inventories";

        String selectSQL = "SELECT id, xp FROM " + tableName + " WHERE xp <> 0";
        String updateSQL = "UPDATE " + tableName + " SET xp = ? WHERE id = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSQL);
             PreparedStatement updateStmt = connection.prepareStatement(updateSQL);
             ResultSet rs = selectStmt.executeQuery()) {

            connection.setAutoCommit(false);

            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                double oldXp = rs.getDouble("xp");

                double newXp = convertXp(oldXp);

                updateStmt.setDouble(1, newXp);
                updateStmt.setInt(2, id);
                updateStmt.addBatch();

                count++;

                if (count % BATCH_SIZE == 0) {
                    updateStmt.executeBatch();
                    connection.commit();
                    plugin.getLogger().info("Converted " + count + " XP records...");
                }
            }

            updateStmt.executeBatch();
            connection.commit();

            plugin.getLogger().info("XP Conversion complete! Total records converted: " + count);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            plugin.getLogger().severe("Error during XP conversion:");
            e.printStackTrace();
        } finally {
            try {
                // Make sure to reset auto-commit back to true after the operation is complete
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to reset auto-commit mode to true!");
                e.printStackTrace();
            }
        }
        return false;
    }
}