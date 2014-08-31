package me.eccentric_nz.gamemodeinventories;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModeInventories extends JavaPlugin {

    private GameModeInventoriesInventory inventoryHandler;
    protected static GameModeInventories plugin;
    GameModeInventoriesDBConnection service;
    private GameModeInventoriesBlock block;
    private final List<String> creativeBlocks = new ArrayList<String>();
    private final List<Material> blackList = new ArrayList<Material>();
    public final String MY_PLUGIN_NAME = ChatColor.GOLD + "[GameModeInventories] " + ChatColor.RESET;
    private GameModeInventoriesMessage m;
    private CoreProtectAPI cpAPI;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        GameModeInventoriesConfig tc = new GameModeInventoriesConfig(this);
        tc.checkConfig();

        service = GameModeInventoriesDBConnection.getInstance();
        loadDatabase();
        PluginManager pm = Bukkit.getServer().getPluginManager();
        // update database add and populate uuid fields
        if (!getConfig().getBoolean("uuid_conversion_done") && getConfig().getString("storage.database").equals("sqlite")) {
            GameModeInventoriesUUIDConverter uc = new GameModeInventoriesUUIDConverter(this);
            if (!uc.convert()) {
                // conversion failed
                System.out.println("[GameModeInventories]" + ChatColor.RED + "UUID conversion failed, disabling...");
                pm.disablePlugin(this);
                return;
            } else {
                getConfig().set("uuid_conversion_done", true);
                saveConfig();
                System.out.println("[GameModeInventories] UUID conversion successful :)");
            }
        }
        m = new GameModeInventoriesMessage(this);
        m.getMessages();
        inventoryHandler = new GameModeInventoriesInventory();
        pm.registerEvents(new GameModeInventoriesListener(this), this);
        pm.registerEvents(new GameModeInventoriesDeath(this), this);
        pm.registerEvents(new GameModeInventoriesBlockListener(this), this);
        pm.registerEvents(new GameModeInventoriesPistonListener(this), this);
        pm.registerEvents(new GameModeInventoriesCommandListener(this), this);
        pm.registerEvents(new GameModeInventoriesWorldListener(this), this);
        GameModeInventoriesCommands command = new GameModeInventoriesCommands(this);
        getCommand("gmi").setExecutor(command);
        getCommand("gmi").setTabCompleter(command);
        block = new GameModeInventoriesBlock(this);
        block.loadBlocks();
        loadBlackList();
        cpAPI = getCP();
    }

    @Override
    public void onDisable() {
        boolean savexp = getConfig().getBoolean("xp");
        boolean savearmour = getConfig().getBoolean("armor");
        boolean saveenderchest = getConfig().getBoolean("enderchest");
        boolean potions = getConfig().getBoolean("remove_potions");
        boolean attributes = plugin.getConfig().getBoolean("custom_attributes");
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.hasPermission("gamemodeinventories.use")) {
                if (p.isOnline()) {
                    inventoryHandler.switchInventories(p, p.getInventory(), savexp, savearmour, saveenderchest, potions, attributes, p.getGameMode());
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

    /**
     * Sets up the database.
     */
    private void loadDatabase() {
        String dbtype = getConfig().getString("storage.database");
        try {
            if (dbtype.equals("sqlite")) {
                String path = getDataFolder() + File.separator + "GMI.db";
                service.setConnection(path);
                GameModeInventoriesSQLite sqlite = new GameModeInventoriesSQLite(this);
                sqlite.createTables();
            } else {
                service.setConnection();
                GameModeInventoriesMySQL mysql = new GameModeInventoriesMySQL(this);
                mysql.createTables();
            }
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(MY_PLUGIN_NAME + "Connection and Tables Error: " + e);
        }
    }

    /**
     * Loads CoreProtect support if available
     *
     * @return the CoreProtectAPI
     */
    public CoreProtectAPI getCP() {
        CoreProtect cp = (CoreProtect) getServer().getPluginManager().getPlugin("CoreProtect");
        // Check that CoreProtect is loaded
        if (cp == null || !(cp instanceof CoreProtect)) {
            return null;
        }
        // Check that the API is enabled
        CoreProtectAPI CoreProtect = cp.getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }
        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 2) {
            return null;
        }
        getServer().getConsoleSender().sendMessage(MY_PLUGIN_NAME + "Connecting to CoreProtect");
        return CoreProtect;
    }

    public CoreProtectAPI getCPAPI() {
        return cpAPI;
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
                getServer().getConsoleSender().sendMessage(MY_PLUGIN_NAME + String.format(m.getMessage().get("INVALID_MATERIAL"), s));
            }
        }
    }

    public GameModeInventoriesMessage getM() {
        return m;
    }
}
