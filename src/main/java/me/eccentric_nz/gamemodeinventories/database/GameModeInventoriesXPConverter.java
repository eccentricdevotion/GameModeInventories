package me.eccentric_nz.gamemodeinventories.database;

import me.eccentric_nz.gamemodeinventories.GameModeInventories;
import me.eccentric_nz.gamemodeinventories.GameModeInventoriesXPCalculator;
import org.bukkit.util.FileUtil;
import java.io.File;
import java.util.Arrays;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class GameModeInventoriesXPConverter {
    private final GameModeInventories plugin;
    private static final int BATCH_SIZE = 1000;

    public GameModeInventoriesXPConverter(GameModeInventories plugin) {
        this.plugin = plugin;
        initLookupTables(1000000);
    }

    // Old methods
    private static int xpTotalToReachLevel[];

    private static void initLookupTables(int maxLevel) {
        xpTotalToReachLevel = new int[maxLevel];

        for (int i = 0; i < xpTotalToReachLevel.length; i++) {
            xpTotalToReachLevel[i] = i >= 30
                    ? (int)(3.5 * i * i - 151.5 * i + 2220)
                    : i >= 16
                    ? (int)(1.5 * i * i - 29.5 * i + 360)
                    : 17 * i;
        }

    }

    private static int oldGetLevelForExp(int exp) {
        if (exp <= 0) {
            return 0;
        }
        if (exp > xpTotalToReachLevel[xpTotalToReachLevel.length - 1]) {
            // need to extend the lookup tables
            int newMax = oldCalculateLevelForExp(exp) * 2;
            initLookupTables(newMax);
        }
        int pos = Arrays.binarySearch(xpTotalToReachLevel, exp);
        return pos < 0 ? -pos - 2 : pos;
    }

    private static int oldCalculateLevelForExp(int exp) {
        int level = 0;
        int curExp = 7; // level 1
        int incr = 10;

        while (curExp <= exp) {
            curExp += incr;
            level++;
            incr += (level % 2 == 0) ? 3 : 4;
        }
        return level;
    }

    private static int oldGetXpNeededToLevelUp(int level) {
        return level > 30 ? 62 + (level - 30) * 7 : level >= 16 ? 17 + (level - 15) * 3 : 17;
    }

    private static int oldGetXpForLevel(int level) {
        if (level >= xpTotalToReachLevel.length) {
            initLookupTables(level * 2);
        }
        return xpTotalToReachLevel[level];
    }

    // Conversion
    private double convertXp(double oldXp) {
        if(oldXp < 0) return 0;
        int oldLevel = oldGetLevelForExp((int)oldXp);
        double oldPct = (oldXp - oldGetXpForLevel(oldLevel)) / (double) (oldGetXpNeededToLevelUp(oldLevel));
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