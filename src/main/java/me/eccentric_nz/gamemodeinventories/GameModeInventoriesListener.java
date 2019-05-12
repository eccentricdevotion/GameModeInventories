package me.eccentric_nz.gamemodeinventories;

import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;
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

public class GameModeInventoriesListener implements Listener {

    private final GameModeInventories plugin;
    List<Material> containers = new ArrayList<>();

    public GameModeInventoriesListener(GameModeInventories plugin) {
        this.plugin = plugin;
        containers.add(Material.ANVIL);
        containers.add(Material.BEACON);
        containers.add(Material.BLACK_SHULKER_BOX);
        containers.add(Material.BLAST_FURNACE);
        containers.add(Material.BLUE_SHULKER_BOX);
        containers.add(Material.BREWING_STAND);
        containers.add(Material.BROWN_SHULKER_BOX);
        containers.add(Material.CARTOGRAPHY_TABLE);
        containers.add(Material.CHEST);
        containers.add(Material.CHIPPED_ANVIL);
        containers.add(Material.CYAN_SHULKER_BOX);
        containers.add(Material.DAMAGED_ANVIL);
        containers.add(Material.DISPENSER);
        containers.add(Material.DROPPER);
        containers.add(Material.ENCHANTING_TABLE);
        containers.add(Material.ENDER_CHEST);
        containers.add(Material.FLETCHING_TABLE);
        containers.add(Material.FURNACE);
        containers.add(Material.GRAY_SHULKER_BOX);
        containers.add(Material.GREEN_SHULKER_BOX);
        containers.add(Material.GRINDSTONE);
        containers.add(Material.HOPPER);
        containers.add(Material.JUKEBOX);
        containers.add(Material.LIGHT_BLUE_SHULKER_BOX);
        containers.add(Material.LIGHT_GRAY_SHULKER_BOX);
        containers.add(Material.LIME_SHULKER_BOX);
        containers.add(Material.LOOM);
        containers.add(Material.MAGENTA_SHULKER_BOX);
        containers.add(Material.ORANGE_SHULKER_BOX);
        containers.add(Material.PINK_SHULKER_BOX);
        containers.add(Material.PURPLE_SHULKER_BOX);
        containers.add(Material.RED_SHULKER_BOX);
        containers.add(Material.SMITHING_TABLE);
        containers.add(Material.SMOKER);
        containers.add(Material.STONECUTTER);
        containers.add(Material.TRAPPED_CHEST);
        containers.add(Material.WHITE_SHULKER_BOX);
        containers.add(Material.YELLOW_SHULKER_BOX);
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
                        Connection connection = null;
                        PreparedStatement statement = null;
                        ResultSet rs = null;
                        try {
                            connection = GameModeInventoriesConnectionPool.dbc();
                            if (connection != null && !connection.isClosed()) {
                                // check if the player has a record for this world
                                statement = connection.prepareStatement("SELECT * FROM worlds WHERE uuid = ? AND world = ?");
                                statement.setString(1, uuid);
                                statement.setString(2, plugin.getConfig().getString("creative_world.world"));
                                rs = statement.executeQuery();
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
                        } finally {
                            try {
                                if (rs != null) {
                                    rs.close();
                                }
                                if (statement != null) {
                                    statement.close();
                                }
                                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                                    connection.close();
                                }
                            } catch (SQLException e) {
                                System.err.println("Could not close resultsets, statements or connection [worlds], " + e);
                            }
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
    public void onInventoryOpen(PlayerInteractEvent event) {
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
                        if (!is.getType().equals(Material.AIR)) {
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
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (plugin.getConfig().getBoolean("no_pickups")) {
            Player p = (Player) event.getEntity();
            GameMode gm = p.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "items", plugin)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_PICKUP"));
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
        return !m.equals(Material.AIR) && m.isBlock();
    }
}
