/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesDBConnection {

    private static final GameModeInventoriesDBConnection instance = new GameModeInventoriesDBConnection();
    private boolean isMySQL;

    public static synchronized GameModeInventoriesDBConnection getInstance() {
        return instance;
    }
    public Connection connection = null;
    public Statement statement = null;

    public void setConnection(String path) throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public void setIsMySQL(boolean isMySQL) {
        this.isMySQL = isMySQL;
    }

    public void setConnection() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find the driver in the classpath!", e);
        }
        String host = "jdbc:" + GameModeInventories.plugin.getConfig().getString("storage.mysql.url") + "?autoReconnect=true";
        String user = GameModeInventories.plugin.getConfig().getString("storage.mysql.user");
        String pass = GameModeInventories.plugin.getConfig().getString("storage.mysql.password");
        try {
            connection = DriverManager.getConnection(host, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot connect the database!", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     *
     * @return an exception
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }

    /**
     * Test the database connection
     *
     * @param connection
     * @throws java.sql.SQLException
     */
    public void testConnection(Connection connection) throws SQLException {
        if (isMySQL) {
            try {
                statement = connection.createStatement();
                statement.executeQuery("SELECT 1");
            } catch (SQLException e) {
                try {
                    this.setConnection();
                } catch (Exception ex) {
                    GameModeInventories.plugin.debug("Could not re-connect to database!");
                }
            }
        }
    }
}
