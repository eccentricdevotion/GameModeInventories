/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import me.botsko.prism.Prism;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesBlockLogger {

    private final GameModeInventories plugin;
    private CoreProtectAPI coreProtectAPI = null;
    private Consumer logBlockConsumer = null;
    private Prism prism = null;
    private GMIBlockLogger whichLogger;
    private boolean logging = false;

    public GameModeInventoriesBlockLogger(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public enum GMIBlockLogger {

        CORE_PROTECT, LOG_BLOCK, PRISM
    }

    public CoreProtectAPI getCoreProtectAPI() {
        return coreProtectAPI;
    }

    public Consumer getLogBlockConsumer() {
        return logBlockConsumer;
    }

    public Prism getPrism() {
        return prism;
    }

    public GMIBlockLogger getWhichLogger() {
        return whichLogger;
    }

    public boolean isLogging() {
        return logging;
    }

    public void enableLogger() {
        PluginManager pm = plugin.getServer().getPluginManager();
        if (pm.isPluginEnabled("CoreProtect")) {
            CoreProtect cp = (CoreProtect) pm.getPlugin("CoreProtect");
            // Check that CoreProtect is loaded
            if (cp == null || !(cp instanceof CoreProtect)) {
                return;
            }
            // Check that the API is enabled
            CoreProtectAPI CoreProtect = cp.getAPI();
            if (CoreProtect.isEnabled() == false) {
                return;
            }
            // Check that a compatible version of the API is loaded
            if (CoreProtect.APIVersion() < 2) {
                return;
            }
            plugin.getServer().getConsoleSender().sendMessage(plugin.MY_PLUGIN_NAME + "Connecting to CoreProtect");
            this.coreProtectAPI = CoreProtect;
            this.whichLogger = GMIBlockLogger.CORE_PROTECT;
            this.logging = true;
        }
        if (pm.isPluginEnabled("LogBlock")) {
            LogBlock lb = (LogBlock) pm.getPlugin("LogBlock");
            if (lb != null) {
                plugin.getServer().getConsoleSender().sendMessage(plugin.MY_PLUGIN_NAME + "Connecting to LogBlock");
                this.logBlockConsumer = lb.getConsumer();
                this.whichLogger = GMIBlockLogger.LOG_BLOCK;
                this.logging = true;
            }
        }
        if (pm.isPluginEnabled("Prism")) {
            Prism tmp_prism = (Prism) pm.getPlugin("Prism");
            if (tmp_prism != null) {
                plugin.getServer().getConsoleSender().sendMessage(plugin.MY_PLUGIN_NAME + "Connecting to Prism");
                this.prism = tmp_prism;
                this.whichLogger = GMIBlockLogger.PRISM;
                this.logging = true;
            }
        }
    }
}
