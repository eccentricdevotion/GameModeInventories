package me.eccentric_nz.gamemodeinventories.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

import java.io.File;

public class GameModeInventoriesSQLiteConnectionPool {

    private final HikariDataSource hikari;

    public GameModeInventoriesSQLiteConnectionPool(GameModeInventories plugin) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        HikariConfig config = new HikariConfig();
        config.setPoolName("GMISQLitePool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "GMI.db");
        config.setConnectionTestQuery("SELECT 1");
        config.setMinimumIdle(1);
        int pool_size = plugin.getConfig().getInt("storage.mysql.pool_size");
        config.setMaximumPoolSize(pool_size);
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.addDataSourceProperty("databaseName", "GMI");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "1024");
        hikari = new HikariDataSource(config);
    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}
