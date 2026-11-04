package me.eccentric_nz.gamemodeinventories;

import io.papermc.paper.event.player.PlayerPickBlockEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Campfire;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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
        if (p.isDead()) {
            return;
        }
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
                        // get last known position in world
                        String uuid = p.getUniqueId().toString();
                        // player changed worlds, record last location
                        // check if the player has a record for this world
                        try (
                                Connection connection = plugin.getDatabaseConnection();
                                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + plugin.getPrefix() + "worlds WHERE uuid = ? AND world = ?")
                        ) {
                            statement.setString(1, uuid);
                            statement.setString(2, plugin.getConfig().getString("creative_world.world"));
                            try (ResultSet rs = statement.executeQuery()) {
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
        onEntityClick(event);
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

    @EventHandler(ignoreCancelled = true)
    public void onPickBlock(PlayerPickBlockEvent event) {
        // ctrl + middle-click copies the block's contents
        if (event.isIncludeData() && plugin.getConfig().getBoolean("restrict_creative")) {
            Player p = event.getPlayer();
            Block b = event.getBlock();
            if (p.getGameMode().equals(GameMode.CREATIVE) && (containers.contains(b.getType()) || hasContents(b.getState(false))) && !GameModeInventoriesBypass.canBypassContainer(p, "copy", plugin)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_PICK_DATA"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onContainerPlace(BlockPlaceEvent event) {
        // empty pre-filled containers e.g. from saved hotbars
        if (plugin.getConfig().getBoolean("restrict_creative")) {
            Player p = event.getPlayer();
            if (p.getGameMode().equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypassContainer(p, "place", plugin)) {
                BlockState state = event.getBlockPlaced().getState(false);
                if (state instanceof Campfire campfire) {
                    for (int i = 0; i < campfire.getSize(); i++) {
                        campfire.setItem(i, null);
                    }
                } else {
                    Inventory inv = getBlockInventory(state);
                    if (inv != null) {
                        inv.clear();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onContainerBreak(BlockBreakEvent event) {
        // the contents would drop, and may have been put there by survival players
        if (plugin.getConfig().getBoolean("restrict_creative") && !plugin.getConfig().getBoolean("allow_creative_container_break")) {
            Player p = event.getPlayer();
            if (p.getGameMode().equals(GameMode.CREATIVE) && hasContents(event.getBlock().getState(false)) && !GameModeInventoriesBypass.canBypassContainer(p, "break", plugin)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_CONTAINER_BREAK"));
                }
            }
        }
    }

    /**
     * Gets the inventory of this block only - for a double chest, getInventory() would include the other half.
     */
    private Inventory getBlockInventory(BlockState state) {
        if (state instanceof Chest chest) {
            return chest.getBlockInventory();
        }
        if (state instanceof InventoryHolder holder) {
            return holder.getInventory();
        }
        return null;
    }

    private boolean hasContents(BlockState state) {
        if (state instanceof Campfire campfire) {
            for (int i = 0; i < campfire.getSize(); i++) {
                if (campfire.getItem(i) != null && !campfire.getItem(i).getType().isAir()) {
                    return true;
                }
            }
            return false;
        }
        Inventory inv = getBlockInventory(state);
        return inv != null && !inv.isEmpty();
    }

    private boolean isBlock(Material m) {
        return !m.isAir() && m.isBlock();
    }
}
