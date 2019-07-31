package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class GameModeInventoriesVillagerListener implements Listener {

    private final GameModeInventories plugin;

    public GameModeInventoriesVillagerListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (!plugin.getConfig().getBoolean("no_villager_trade")) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        event.setCancelled(true);
        if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
            player.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_TRADE"));
        }
    }
}
