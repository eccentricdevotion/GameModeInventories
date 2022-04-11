/*
 *  Copyright 2015 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesMySQLConnectionPool {

    private static boolean isMySQL = false;
    private final HikariDataSource hikari;

    public GameModeInventoriesMySQLConnectionPool(GameModeInventories plugin) throws ClassNotFoundException {
        isMySQL = true;
        Class.forName("com.mysql.jdbc.Driver");
        String host = plugin.getConfig().getString("storage.mysql.server");
        String port = plugin.getConfig().getString("storage.mysql.port");
        String databaseName = plugin.getConfig().getString("storage.mysql.database");
        String user = plugin.getConfig().getString("storage.mysql.user");
        String password = plugin.getConfig().getString("storage.mysql.password");
        int pool_size = plugin.getConfig().getInt("storage.mysql.pool_size");
        String url = String.format("jdbc:mysql://%s:%s/%s", host, port, databaseName);
        if (!plugin.getConfig().getBoolean("storage.mysql.useSSL")) {
            url += "?useSSL=false";
        }
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(pool_size);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("databaseName", databaseName);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "1024");
        if (plugin.getConfig().getBoolean("storage.mysql.test_connection")) {
            config.setConnectionTestQuery("SELECT 1");
        }
        hikari = new HikariDataSource(config);
    }

    public static boolean isIsMySQL() {
        return isMySQL;
    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}
