/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesSQLiteConnection {

    private static final GameModeInventoriesSQLiteConnection INSTANCE = new GameModeInventoriesSQLiteConnection();
    public Connection connection = null;

    public static synchronized GameModeInventoriesSQLiteConnection getINSTANCE() {
        return INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(String path) throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    /**
     * @return an exception
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
}
