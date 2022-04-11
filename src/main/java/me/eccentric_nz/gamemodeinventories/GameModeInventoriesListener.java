package me.eccentric_nz.gamemodeinventories;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class GameModeInventoriesListener implements Listener {

    private final GameModeInventories plugin;
    List<Material> containers = new ArrayList<>();

    public GameModeInventoriesListener(GameModeInventories plugin) {
        this.plugin = plugin;
        for (String m : this.plugin.getConfig().getStringList("containers")) {
            try {
                containers.add(Material.valueOf(m));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().log(Level.INFO, plugin.MY_PLUGIN_NAME + "Illegal material name " + m + " in containers list!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player p = event.getPlayer();
        GameMode newGM = event.getNewGameMode();
        if (newGM.equals(GameMode.SPECTATOR) && plugin.getConfig().getBoolean("restrict_spectator") && !p.hasPermission("gamemodeinventories.spectator")) {
            event.setCancelled(true);
            p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_SPECTATOR"));
            return;
        }
        if (p.hasPermission("gamemodeinventories.use")) {
            if (p.isOnline()) {
                plugin.getInventoryHandler().switchInventories(p, newGM);
                if (newGM.equals(GameMode.CREATIVE) && plugin.getConfig().getBoolean("creative_world.switch_to")) {
                    // get spawn location
                    Location loc = plugin.getServer().getWorld(plugin.getConfig().getString("creative_world.world")).getSpawnLocation();
                    if (plugin.getConfig().getString("creative_world.location").equals("last_known")) {
                        //get last known position in world
                        String uuid = p.getUniqueId().toString();
                        // player changed worlds, record last location
                        // check if the player has a record for this world
                        try (
                                Connection connection = plugin.getDatabaseConnection();
                                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + plugin.getPrefix() + "worlds WHERE uuid = ? AND world = ?");
                        ) {
                            statement.setString(1, uuid);
                            statement.setString(2, plugin.getConfig().getString("creative_world.world"));
                            try (ResultSet rs = statement.executeQuery();) {
                                if (rs.next()) {
                                    World w = plugin.getServer().getWorld(rs.getString("world"));
                                    if (w != null) {
                                        double x = rs.getDouble("x");
                                        double y = rs.getDouble("y");
                                        double z = rs.getDouble("z");
                                        float yaw = rs.getFloat("yaw");
                                        float pitch = rs.getFloat("pitch");
                                        // send to last location
                                        loc = new Location(w, x, y, z, yaw, pitch);
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            plugin.debug("Could not get creative world location, " + e);
                        }
                    }
                    if (loc != null) {
                        p.teleport(loc);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.getConfig().getBoolean("restrict_creative")) {
            Block b = event.getClickedBlock();
            if (b != null) {
                Player p = event.getPlayer();
                if (p.isSneaking() && isBlock(p.getInventory().getItemInMainHand().getType())) {
                    return;
                }
                Material m = b.getType();
                GameMode gm = p.getGameMode();
                if (gm.equals(GameMode.CREATIVE) && containers.contains(m) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                        p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_INVENTORY"));
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (plugin.getConfig().getBoolean("no_drops")) {
            Inventory inv = event.getInventory();
            if (inv.getType().equals(InventoryType.WORKBENCH)) {
                Player p = (Player) event.getPlayer();
                if (p.getGameMode().equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin)) {
                    boolean empty = true;
                    for (ItemStack is : inv.getContents()) {
                        if (!is.getType().isAir()) {
                            empty = false;
                        }
                    }
                    if (!empty) {
                        inv.clear();
                        if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                            p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_WORKBENCH_DROPS"));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (plugin.getConfig().getBoolean("restrict_creative")) {
            Entity entity = event.getRightClicked();
            Player p = event.getPlayer();
            if (p.getGameMode().equals(GameMode.CREATIVE) && plugin.getInventoryHandler().isInstanceOf(entity) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin)) {
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_INVENTORY"));
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        //treat it the same as interacting with an entity in general
        onEntityClick((PlayerInteractEntityEvent) event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (plugin.getConfig().getBoolean("no_drops")) {
            Player p = event.getPlayer();
            GameMode gm = p.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "items", plugin)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_PLAYER_DROPS"));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void noPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && plugin.getConfig().getBoolean("no_pickups")) {
            GameMode gm = player.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(player, "items", plugin)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    player.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_PICKUP"));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void noHorseInventory(InventoryOpenEvent event) {
        if (plugin.getConfig().getBoolean("restrict_creative") && plugin.getInventoryHandler().isInstanceOf(event.getInventory().getHolder())) {
            Player p = (Player) event.getPlayer();
            GameMode gm = p.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_HORSE"));
                }
            }
        }
    }

    private boolean isBlock(Material m) {
        return !m.isAir() && m.isBlock();
    }
}
