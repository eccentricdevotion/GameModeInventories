/*
 *  Copyright 2015 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesConnectionPool {

    private static HikariDataSource hikari;
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
                con = hikari.getConnection();
            } catch (SQLException e) {
                GameModeInventories.plugin.debug("Could not get database connection: " + e.getMessage());
            }
        } else {
            service = GameModeInventoriesSQLiteConnection.getInstance();
            con = service.getConnection();
        }
        return con;
    }

    public GameModeInventoriesConnectionPool() throws ClassNotFoundException {
        isMySQL = true;
        Class.forName("com.mysql.jdbc.Driver");
        String host = GameModeInventories.plugin.getConfig().getString("storage.mysql.server");
        String port = GameModeInventories.plugin.getConfig().getString("storage.mysql.port");
        String databaseName = GameModeInventories.plugin.getConfig().getString("storage.mysql.database");
        String user = GameModeInventories.plugin.getConfig().getString("storage.mysql.user");
        String password = GameModeInventories.plugin.getConfig().getString("storage.mysql.password");
        int pool_size = GameModeInventories.plugin.getConfig().getInt("storage.mysql.pool_size");
        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(pool_size);
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", databaseName);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", password);
    }

    public static boolean isIsMySQL() {
        return isMySQL;
    }

    public static boolean testConnection(Connection connection) {
        if (isMySQL) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.executeQuery("SELECT 1");
                return true;
            } catch (Exception e) {
                GameModeInventories.plugin.debug("Database connection was NULL!");
                return false;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                        GameModeInventories.plugin.debug("Could not close test statement!");
                    }
                }
            }
        }
        return true;
    }
}
