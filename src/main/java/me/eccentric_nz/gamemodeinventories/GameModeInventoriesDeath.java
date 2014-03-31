package me.eccentric_nz.gamemodeinventories;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameModeInventoriesDeath implements Listener {

    private final GameModeInventories plugin;
    GameModeInventoriesDatabase service = GameModeInventoriesDatabase.getInstance();
    private final boolean force;
    private final GameMode mode;

    public GameModeInventoriesDeath(GameModeInventories plugin) {
        this.plugin = plugin;
        ServerValues sv = this.getServerForceGamemode();
        this.force = sv.getForce();
        this.mode = sv.getMode();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        assert (event.getEntity() instanceof Player);
        Player p = (Player) event.getEntity();
        if (p.hasPermission("gamemodeinventories.death") && plugin.getConfig().getBoolean("save_on_death")) {
            // save their inventory
            plugin.getInventoryHandler().saveOnDeath(p);
            event.getDrops().clear();
        }
        if (p.getGameMode().equals(GameMode.CREATIVE) && plugin.getConfig().getBoolean("no_drops")) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = (Player) event.getPlayer();
        if (p.hasPermission("gamemodeinventories.use") && force) {
            p.setGameMode(mode);
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("gamemodeinventories.death") && plugin.getConfig().getBoolean("save_on_death")) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getInventoryHandler().restoreOnSpawn(p);
                }
            });
        }
    }

    /**
     * Gets the server values for force-gamemode and gamemode.
     *
     * @return whether the value is true or false.
     */
    private ServerValues getServerForceGamemode() {
        FileInputStream in = null;
        ServerValues sv = new ServerValues();
        try {
            Properties properties = new Properties();
            String path = "server.properties";
            in = new FileInputStream(path);
            properties.load(in);
            sv.setForce(properties.getProperty("force-gamemode").equalsIgnoreCase("true"));
            int gmode = Integer.parseInt(properties.getProperty("gamemode"));
            switch (gmode) {
                case 1:
                    sv.setMode(GameMode.CREATIVE);
                    break;
                case 2:
                    sv.setMode(GameMode.ADVENTURE);
                    break;
                default:
                    sv.setMode(GameMode.SURVIVAL);
                    break;
            }
        } catch (FileNotFoundException ex) {
            plugin.debug("Could not find server.properties!");
        } catch (IOException ex) {
            plugin.debug("Could not read server.properties!");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                plugin.debug("Could not close server.properties!");
            }
        }
        return sv;
    }

    public class ServerValues {

        private boolean force;
        private GameMode mode;

        public boolean getForce() {
            return force;
        }

        public void setForce(boolean force) {
            this.force = force;
        }

        public GameMode getMode() {
            return mode;
        }

        public void setMode(GameMode mode) {
            this.mode = mode;
        }
    }
}
