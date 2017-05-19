package me.eccentric_nz.gamemodeinventories;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesBlocksConverter;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesMySQL;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesQueueDrain;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesRecordingTask;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesSQLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class GameModeInventories extends JavaPlugin {

    private GameModeInventoriesInventory inventoryHandler;
    public static GameModeInventories plugin;
    private GameModeInventoriesBlock block;
    private final HashMap<String, List<String>> creativeBlocks = new HashMap<String, List<String>>();
    private final List<Material> blackList = new ArrayList<Material>();
    private final List<Material> noTrackList = new ArrayList<Material>();
    private final List<String> points = new ArrayList<String>();
    private final List<UUID> stands = new ArrayList<UUID>();
    public final String MY_PLUGIN_NAME = ChatColor.GOLD + "[GameModeInventories] " + ChatColor.RESET;
    private GameModeInventoriesMessage m;
    private GameModeInventoriesBlockLogger blockLogger;
    public BukkitTask recordingTask;
    private GMIDebug db_level;

    @Override
    public void onEnable() {
        plugin = this;
        PluginManager pm = Bukkit.getServer().getPluginManager();
        Version bukkitversion = getServerVersion(getServer().getVersion());
        Version minversion = new Version("1.9");
        // check CraftBukkit version
        if (bukkitversion.compareTo(minversion) >= 0) {
            saveDefaultConfig();
            GameModeInventoriesConfig tc = new GameModeInventoriesConfig(this);
            tc.checkConfig();
            loadDatabase();
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
            // update database add and populate block fields
            if (!getConfig().getBoolean("blocks_conversion_done")) {
                new GameModeInventoriesBlocksConverter(this).convertBlocksTable();
                getConfig().set("blocks_conversion_done", true);
                saveConfig();
                System.out.println("[GameModeInventories] Blocks conversion successful :)");
            }
            // check if creative world exists
            if (getConfig().getBoolean("creative_world.switch_to")) {
                World creative = getServer().getWorld(getConfig().getString("creative_world.world"));
                if (creative == null) {
                    getConfig().set("creative_world.switch_to", false);
                    saveConfig();
                    System.out.println("[GameModeInventories] Creative world specified in the config was not found, disabling world switching!");
                }
            }
            block = new GameModeInventoriesBlock(this);
            m = new GameModeInventoriesMessage(this);
            m.updateMessages();
            m.getMessages();
            try {
                db_level = GMIDebug.valueOf(getConfig().getString("debug_level"));
            } catch (IllegalArgumentException e) {
                db_level = GMIDebug.ERROR;
            }
            inventoryHandler = new GameModeInventoriesInventory();
            pm.registerEvents(new GameModeInventoriesListener(this), this);
            pm.registerEvents(new GameModeInventoriesChunkLoadListener(this), this);
            pm.registerEvents(new GameModeInventoriesDeath(this), this);
            pm.registerEvents(new GameModeInventoriesBlockListener(this), this);
            if (getConfig().getBoolean("track_creative_place.dont_track_is_whitelist")) {
                pm.registerEvents(new GameModeInventoriesTrackWhiteListener(this), this);
            } else {
                pm.registerEvents(new GameModeInventoriesTrackBlackListener(this), this);
            }
            pm.registerEvents(new GameModeInventoriesPistonListener(this), this);
            pm.registerEvents(new GameModeInventoriesCommandListener(this), this);
            pm.registerEvents(new GameModeInventoriesWorldListener(this), this);
            pm.registerEvents(new GameModeInventoriesEntityListener(this), this);
            pm.registerEvents(new GameModeInventoriesPhysicsListener(this), this);
            GameModeInventoriesCommands command = new GameModeInventoriesCommands(this);
            getCommand("gmi").setExecutor(command);
            getCommand("gmi").setTabCompleter(command);
            new GameModeInventoriesStand(this).loadStands();
            loadBlackList();
            loadNoTrackList();
            setUpBlockLogger();
            actionRecorderTask();
        } else {
            getServer().getConsoleSender().sendMessage(MY_PLUGIN_NAME + ChatColor.RED + "This plugin requires CraftBukkit/Spigot 1.9 or higher, disabling...");
            pm.disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        boolean savexp = getConfig().getBoolean("xp");
        boolean savearmour = getConfig().getBoolean("armor");
        boolean saveenderchest = getConfig().getBoolean("enderchest");
        boolean potions = getConfig().getBoolean("remove_potions");
        boolean attributes = getConfig().getBoolean("custom_attributes");
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.hasPermission("gamemodeinventories.use")) {
                if (p.isOnline()) {
                    inventoryHandler.switchInventories(p, p.getInventory(), savexp, savearmour, saveenderchest, potions, attributes, p.getGameMode());
                }
            }
        }
        new GameModeInventoriesStand(this).saveStands();
        new GameModeInventoriesQueueDrain(this).forceDrainQueue();
    }

    private Version getServerVersion(String s) {
        Pattern pat = Pattern.compile("\\((.+?)\\)", Pattern.DOTALL);
        Matcher mat = pat.matcher(s);
        String v;
        if (mat.find()) {
            String[] split = mat.group(1).split(" ");
            String[] tmp = split[1].split("-");
            if (tmp.length > 1) {
                v = tmp[0];
            } else {
                v = split[1];
            }
        } else {
            v = "1.7.10";
        }
        return new Version(v);
    }

    /**
     * Sets up the database.
     */
    private void loadDatabase() {
        String dbtype = getConfig().getString("storage.database");
        try {
            if (dbtype.equals("sqlite")) {
                String path = getDataFolder() + File.separator + "GMI.db";
                GameModeInventoriesConnectionPool pool = new GameModeInventoriesConnectionPool(path);
                GameModeInventoriesSQLite sqlite = new GameModeInventoriesSQLite(this);
                sqlite.createTables();
            } else {
                GameModeInventoriesConnectionPool pool = new GameModeInventoriesConnectionPool();
                GameModeInventoriesMySQL mysql = new GameModeInventoriesMySQL(this);
                mysql.createTables();
            }
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(MY_PLUGIN_NAME + "Connection and Tables Error: " + e);
        }
    }

    /**
     * Loads block logger support if available
     */
    public void setUpBlockLogger() {
        this.blockLogger = new GameModeInventoriesBlockLogger(this);
        blockLogger.enableLogger();
    }

    public GameModeInventoriesBlockLogger getBlockLogger() {
        return blockLogger;
    }

    public void debug(Object o, GMIDebug b) {
        if (getConfig().getBoolean("debug") == true) {
            if (b.equals(db_level) || b.equals(GMIDebug.ALL)) {
                System.out.println("[GameModeInventories Debug] " + o);
            }
        }
    }

    public void debug(Object o) {
        debug(o, GMIDebug.ERROR);
    }

    public GameModeInventoriesInventory getInventoryHandler() {
        return inventoryHandler;
    }

    public GameModeInventoriesBlock getBlock() {
        return block;
    }

    public HashMap<String, List<String>> getCreativeBlocks() {
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

    public List<Material> getNoTrackList() {
        return noTrackList;
    }

    private void loadNoTrackList() {
        List<String> ntl = getConfig().getStringList("track_creative_place.dont_track");
        for (String s : ntl) {
            try {
                noTrackList.add(Material.valueOf(s));
            } catch (IllegalArgumentException iae) {
                getServer().getConsoleSender().sendMessage(MY_PLUGIN_NAME + String.format(m.getMessage().get("INVALID_MATERIAL_TRACK"), s));
            }
        }
    }

    public List<String> getPoints() {
        return points;
    }

    public List<UUID> getStands() {
        return stands;
    }

    public GameModeInventoriesMessage getM() {
        return m;
    }

    public void actionRecorderTask() {
        int recorder_tick_delay = 3;
        // we schedule it once, it will reschedule itself
        recordingTask = getServer().getScheduler().runTaskLaterAsynchronously(this, new GameModeInventoriesRecordingTask(this), recorder_tick_delay);
    }
}
