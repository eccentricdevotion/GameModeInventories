package me.eccentric_nz.gamemodeinventories;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModeInventories extends JavaPlugin {

    private GameModeInventoriesInventory inventoryHandler;
    protected static GameModeInventories plugin;
    GameModeInventoriesDatabase service;
    private GameModeInventoriesBlock block;
    private final List<String> creativeBlocks = new ArrayList<String>();
    private final List<Material> blackList = new ArrayList<Material>();

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
        PluginManager pm = Bukkit.getServer().getPluginManager();
        // update database add and populate uuid fields
        if (!getConfig().getBoolean("uuid_conversion_done")) {
            GameModeInventoriesUUIDConverter uc = new GameModeInventoriesUUIDConverter(this);
            if (!uc.convert()) {
                // conversion failed
                System.out.println("[GameModeInventories]" + ChatColor.RED + "UUID conversion failed, disabling...");
                pm.disablePlugin(this);
                return;
            } else {
                getConfig().set("uuid_conversion_done", true);
                System.out.println("[GameModeInventories] UUID conversion successful :)");
            }
        }
        inventoryHandler = new GameModeInventoriesInventory();
        pm.registerEvents(new GameModeInventoriesListener(this), this);
        pm.registerEvents(new GameModeInventoriesDeath(this), this);
        pm.registerEvents(new GameModeInventoriesBlockListener(this), this);
        pm.registerEvents(new GameModeInventoriesPistonListener(this), this);
        pm.registerEvents(new GameModeInventoriesCommandListener(this), this);
        GameModeInventoriesCommands command = new GameModeInventoriesCommands(this);
        getCommand("gmi").setExecutor(command);
        getCommand("gmi").setTabCompleter(command);
        block = new GameModeInventoriesBlock(this);
        block.loadBlocks();
        loadBlackList();
    }

    @Override
    public void onDisable() {
        boolean savexp = getConfig().getBoolean("xp");
        boolean savearmour = getConfig().getBoolean("armor");
        boolean saveenderchest = getConfig().getBoolean("enderchest");
        boolean potions = getConfig().getBoolean("remove_potions");
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.hasPermission("gamemodeinventories.use")) {
                if (p.isOnline()) {
                    inventoryHandler.switchInventories(p, p.getInventory(), savexp, savearmour, saveenderchest, potions, p.getGameMode());
                }
            }
        }
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

    public List<Material> getBlackList() {
        return blackList;
    }

    private void loadBlackList() {
        List<String> bl = getConfig().getStringList("blacklist");
        for (String s : bl) {
            try {
                blackList.add(Material.valueOf(s));
            } catch (IllegalArgumentException iae) {
                getServer().getConsoleSender().sendMessage("[GameModeInventories] Invalid material in blacklist - " + s);
            }
        }
    }
}
