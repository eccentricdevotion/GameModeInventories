/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesBlockLoader;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesChunkLoadListener implements Listener {

    private final GameModeInventories plugin;
    private boolean firstLogin = true;

    public GameModeInventoriesChunkLoadListener(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        Chunk chunk = event.getChunk();
        String world = chunk.getWorld().getName();
        if (!plugin.getConfig().getStringList("track_creative_place.worlds").contains(world)) {
            return;
        }
        String gmiwc = chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
        if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
            new GameModeInventoriesBlockLoader(plugin, gmiwc).runTaskAsynchronously(plugin);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!plugin.getConfig().getBoolean("track_creative_place.enabled")) {
            return;
        }
        if (firstLogin) {
            firstLogin = false;
            Chunk[] chunks = event.getPlayer().getWorld().getLoadedChunks();
            for (Chunk c : chunks) {
                String gmiwc = c.getWorld().getName() + "," + c.getX() + "," + c.getZ();
                if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
                    new GameModeInventoriesBlockLoader(plugin, gmiwc).runTaskAsynchronously(plugin);
                }
            }
        } else {
            Chunk chunk = event.getPlayer().getLocation().getChunk();
            String gmiwc = chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
            if (!plugin.getCreativeBlocks().containsKey(gmiwc)) {
                plugin.debug(gmiwc, GMIDebug.ALL);
                new GameModeInventoriesBlockLoader(plugin, gmiwc).runTaskAsynchronously(plugin);
            }
        }
    }
}
