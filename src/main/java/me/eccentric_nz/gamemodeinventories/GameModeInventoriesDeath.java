package me.eccentric_nz.gamemodeinventories;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameModeInventoriesDeath implements Listener {

    private final GameModeInventories plugin;
    GameModeInventoriesDatabase service = GameModeInventoriesDatabase.getInstance();

    public GameModeInventoriesDeath(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        assert (event.getEntity() instanceof Player);
        Player p = (Player) event.getEntity();

        if (p.hasPermission("gamemodeinventories.death") && plugin.getConfig().getBoolean("save_on_death")) {
            // save their inventory
            plugin.inventoryHandler.saveOnDeath(p);
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("gamemodeinventories.death") && plugin.getConfig().getBoolean("save_on_death")) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.inventoryHandler.restoreOnSpawn(p);
                }
            });
        }
    }
}
