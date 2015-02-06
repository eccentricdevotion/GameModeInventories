/*
 *  Copyright 2015 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.queue;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.ConnectionPoolDataSource;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesConnectionPool {

    private static ConnectionPoolDataSource dataSource;
    private static GameModeInventoriesPoolManager poolMgr;
    private static boolean isMySQL = false;
    private static GameModeInventoriesSQLiteConnection service;

    public GameModeInventoriesConnectionPool(String path) {
        try {
            service = GameModeInventoriesSQLiteConnection.getInstance();
            service.setConnection(path);
        } catch (Exception e) {
            GameModeInventories.plugin.debug("Database connection failed. " + e.getMessage());
        }
    }

    public static Connection dbc() {
        Connection con = null;
        if (isMySQL) {
            try {
                con = poolMgr.getConnection();
            } catch (final SQLException e) {
                GameModeInventories.plugin.debug("Database connection failed. " + e.getMessage());
                if (!e.getMessage().contains("Pool empty")) {
                    e.printStackTrace();
                }
            }
        } else {
            service = GameModeInventoriesSQLiteConnection.getInstance();
            con = service.getConnection();
        }
        return con;
    }

    /**
     * Attempt to rebuild the pool, useful for reloads and failed database
     * connections being restored
     *
     * @throws java.sql.SQLException
     */
    public static void rebuildPool() throws SQLException {
        // Close pool connections when plugin disables
        if (poolMgr != null) {
            poolMgr.dispose();
        }
        poolMgr = new GameModeInventoriesPoolManager(dataSource, 10);
    }

    public GameModeInventoriesConnectionPool() throws ClassNotFoundException {
        isMySQL = true;
        Class.forName("com.mysql.jdbc.Driver");
        String host = "jdbc:" + GameModeInventories.plugin.getConfig().getString("storage.mysql.url");
        String user = GameModeInventories.plugin.getConfig().getString("storage.mysql.user");
        String pass = GameModeInventories.plugin.getConfig().getString("storage.mysql.password");
        MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
        ds.setUrl(host);
        ds.setUser(user);
        ds.setPassword(pass);
        ds.setAutoReconnect(true);
        ds.setAutoReconnectForConnectionPools(true);
        poolMgr = new GameModeInventoriesPoolManager(ds, 10);
        dataSource = ds;
    }

    public static boolean isIsMySQL() {
        return isMySQL;
    }
}
