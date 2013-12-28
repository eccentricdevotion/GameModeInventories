package me.eccentric_nz.gamemodeinventories;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModeInventories extends JavaPlugin {

    private GameModeInventoriesInventory inventoryHandler;
    protected static GameModeInventories plugin;
    GameModeInventoriesDatabase service;
    private GameModeInventoriesBlock block;
    private List<String> creativeBlocks = new ArrayList<String>();

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        GameModeInventoriesConfig tc = new GameModeInventoriesConfig(this);
        tc.checkConfig();

        service = GameModeInventoriesDatabase.getInstance();
        try {
            String path = getDataFolder() + File.separator + "GMI.db";
            service.setConnection(path);
            service.createTables();
        } catch (Exception e) {
            debug("Connection and Tables Error: " + e);
        }

        inventoryHandler = new GameModeInventoriesInventory();
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new GameModeInventoriesListener(this), this);
        pm.registerEvents(new GameModeInventoriesDeath(this), this);
        pm.registerEvents(new GameModeInventoriesBlockListener(this), this);
        GameModeInventoriesCommands command = new GameModeInventoriesCommands(this);
        getCommand("gmi").setExecutor(command);
        getCommand("gmi").setTabCompleter(command);
        block = new GameModeInventoriesBlock(this);
        block.loadBlocks();
    }

    @Override
    public void onDisable() {
        this.saveConfig();
        try {
            service.connection.close();
        } catch (SQLException e) {
            System.err.println("[GameModeInventories] Could not close database connection: " + e);
        }
    }

    public void debug(Object o) {
        if (getConfig().getBoolean("debug") == true) {
            System.out.println("[GameModeInventories Debug] " + o);
        }
    }

    public GameModeInventoriesInventory getInventoryHandler() {
        return inventoryHandler;
    }

    public GameModeInventoriesBlock getBlock() {
        return block;
    }

    public List<String> getCreativeBlocks() {
        return creativeBlocks;
    }
}
